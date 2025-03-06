package com.example.keepstock.service.order;

import com.example.keepstock.client.AccountServiceClient;
import com.example.keepstock.client.CrmServiceClient;
import com.example.keepstock.dto.customer.CustomerInfo;
import com.example.keepstock.dto.mappers.OrderMapper;
import com.example.keepstock.dto.order.OrderDto;
import com.example.keepstock.dto.order.OrderInfo;
import com.example.keepstock.dto.product.OrderProductRequest;
import com.example.keepstock.dto.product.OrderProductResponse;
import com.example.keepstock.entity.Customer;
import com.example.keepstock.entity.Order;
import com.example.keepstock.entity.OrderedProduct;
import com.example.keepstock.entity.OrderedProductKey;
import com.example.keepstock.entity.Product;
import com.example.keepstock.error.exception.AccessDeniedException;
import com.example.keepstock.error.exception.EntityNotFoundException;
import com.example.keepstock.error.exception.InsufficientStockException;
import com.example.keepstock.error.exception.ValidationException;
import com.example.keepstock.model.OrderStatus;
import com.example.keepstock.repository.CustomerRepository;
import com.example.keepstock.repository.OrderRepository;
import com.example.keepstock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final String ORDER_WITH_ID = "Order with id ";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CrmServiceClient crmServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public UUID addOrder(Long customerId, String deliveryAddress, List<OrderProductRequest> productsRequest) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer with id " + customerId + " not already exists");
        }
        Customer customer = new Customer();
        customer.setId(customerId);
        Order order = new Order();
        order.setDeliveryAddress(deliveryAddress);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);

        List<UUID> productsRequestIds = productsRequest.stream().map(OrderProductRequest::getId).toList();
        Map<UUID, Product> products = productRepository.findAllById(productsRequestIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));


        for (OrderProductRequest productRequest : productsRequest) {
            Product product = products.get(productRequest.getId());

            validateProductAvailability(product, productRequest.getQuantity());

            product.setQuantity(product.getQuantity() - productRequest.getQuantity());

            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setId(new OrderedProductKey(order.getId(), product.getId()));
            orderedProduct.setProduct(product);
            orderedProduct.setOrder(order);
            orderedProduct.setPriceAtOrderTime(product.getPrice());
            orderedProduct.setQuantity(productRequest.getQuantity());

            order.getOrderedProducts().add(orderedProduct);
        }

        Order saveOrder = orderRepository.save(order);
        return saveOrder.getId();
    }

    @Override
    public UUID updateOrder(Long customerId, UUID orderId, String deliveryAddress, List<OrderProductRequest> productsRequest) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> orderNotFound(orderId));

        isCustomerOrderOwner(customerId, existingOrder.getCustomer().getId());

        if (existingOrder.getStatus() != OrderStatus.CREATED) {
            throw new ValidationException(ORDER_WITH_ID + orderId + " is not created");
        }
        Optional.ofNullable(deliveryAddress)
                .filter(addr -> !addr.isBlank())
                .ifPresent(existingOrder::setDeliveryAddress);


        Set<UUID> allProductIds = new HashSet<>();
        productsRequest.forEach(req -> allProductIds.add(req.getId()));
        existingOrder.getOrderedProducts().forEach(op -> allProductIds.add(op.getProduct().getId()));


        Map<UUID, Product> products = productRepository.findAllById(allProductIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        Map<UUID, OrderedProduct> existOrderProducts = existingOrder.getOrderedProducts().stream()
                .collect(Collectors.toMap(op -> op.getProduct().getId(), op -> op));


        for (OrderProductRequest orderProductRequest : productsRequest) {
            UUID productId = orderProductRequest.getId();
            Product product = products.get(productId);
            validateProductAvailability(product, orderProductRequest.getQuantity());
            long orderedQuantity = orderProductRequest.getQuantity();

            Optional.ofNullable(existOrderProducts.get(productId))
                    .ifPresentOrElse(
                            op -> {
                                op.setQuantity(op.getQuantity() + orderedQuantity);
                                op.setPriceAtOrderTime(product.getPrice());
                            },
                            () -> {
                                OrderedProduct orderedProduct = new OrderedProduct();
                                orderedProduct.setProduct(product);
                                orderedProduct.setOrder(existingOrder);
                                orderedProduct.setPriceAtOrderTime(product.getPrice());
                                orderedProduct.setQuantity(orderedQuantity);

                                existingOrder.getOrderedProducts().add(orderedProduct);
                            }
                    );
            product.setQuantity(product.getQuantity() - orderedQuantity);
        }
        return orderRepository.save(existingOrder).getId();
    }

    @Override
    public void deleteOrder(Long customerId, UUID orderId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> orderNotFound(orderId));
        isCustomerOrderOwner(customerId, existingOrder.getCustomer().getId());
        if (existingOrder.getStatus() != OrderStatus.CREATED) {
            throw new ValidationException(ORDER_WITH_ID + orderId + " is not created");
        }
        existingOrder.setStatus(OrderStatus.CANCELLED);

        List<UUID> productIds = existingOrder.getOrderedProducts().stream()
                .map(op -> op.getProduct().getId()).toList();

        Map<UUID, Product> products = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        existingOrder.getOrderedProducts().forEach(op -> {
            Product product = products.get(op.getProduct().getId());
            product.setQuantity(product.getQuantity() + op.getQuantity());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getCustomerOrder(UUID orderId, Long customerId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> orderNotFound(orderId));
        isCustomerOrderOwner(existingOrder.getCustomer().getId(), customerId);
        return getOrderDto(orderId, existingOrder);
    }

    @Override
    public void confirmOrder(UUID orderId, Long customerId) {
        // TODO: Метод будет реализован позже
    }

    @Override
    public void changeOrderStatus(UUID orderId, Long customerId, OrderStatus orderStatus) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> orderNotFound(orderId));
        isCustomerOrderOwner(existingOrder.getCustomer().getId(), customerId);
        if (orderStatus != OrderStatus.CREATED && orderStatus != OrderStatus.CONFIRMED) {
            throw new ValidationException(ORDER_WITH_ID + orderId + " must be created or confirmed");
        }
        existingOrder.setStatus(orderStatus);
    }

    @Override
    public Map<UUID, List<OrderInfo>> getProductOrdersWithClients() {
        List<Order> existOrders = orderRepository.findAllForOrderProductInfo();

        ConcurrentMap<UUID, List<Order>> productToOrdersMap = existOrders.parallelStream()
                .flatMap(order -> order.getOrderedProducts().stream()
                        .map(orderedProduct -> Map.entry(orderedProduct.getProduct().getId(), order)))
                .collect(Collectors.groupingByConcurrent(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        List<String> customerLogins = existOrders.parallelStream()
                .map(order -> order.getCustomer().getLogin())
                .toList();

        CompletableFuture<Map<String, String>> future1 = accountServiceClient.getAccountsAsync(customerLogins);
        CompletableFuture<Map<String, String>> future2 = crmServiceClient.getCustomerInnsAsync(customerLogins);

        CompletableFuture.allOf(future1, future2).join();

        Map<String, String> customerAccounts = future1.join();
        Map<String, String> customerInns = future2.join();

        Map<UUID, List<OrderInfo>> resultOrderInfos = new HashMap<>();

        for (Map.Entry<UUID, List<Order>> entry : productToOrdersMap.entrySet()) {
            UUID productId = entry.getKey();
            List<OrderInfo> orderInfos = entry.getValue().stream()
                    .map(order -> {
                        Customer customer = order.getCustomer();
                        OrderedProduct orderedProduct = order.getOrderedProducts().stream()
                                .filter(op -> op.getProduct().getId().equals(productId))
                                .findFirst()
                                .orElseThrow();

                        CustomerInfo customerInfo = new CustomerInfo();
                        customerInfo.setId(customer.getId());
                        customerInfo.setAccountNumber(customerAccounts.get(customer.getLogin()));
                        customerInfo.setEmail(customer.getEmail());
                        customerInfo.setInn(customerInns.get(customer.getLogin()));

                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setCustomer(customerInfo);
                        orderInfo.setId(order.getId());
                        orderInfo.setStatus(order.getStatus());
                        orderInfo.setDeliveryAddress(order.getDeliveryAddress());
                        orderInfo.setQuantity(orderedProduct.getQuantity());

                        return orderInfo;
                    })
                    .toList();

            resultOrderInfos.put(productId, orderInfos);
        }

        return resultOrderInfos;

    }

    private OrderDto getOrderDto(UUID orderId, Order existingOrder) {
        List<OrderProductResponse> products = orderRepository.findOrderProductByOrderId(orderId);
        OrderDto orderDto = orderMapper.toOrderDto(existingOrder);
        orderDto.setProducts(products);

        BigDecimal totalPrice = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orderDto.setTotalPrice(totalPrice);
        return orderDto;
    }

    private void validateProductAvailability(Product product, Long requestedQuantity) {
        if (product == null) {
            throw new EntityNotFoundException("Product does not exist");
        }
        if (Boolean.FALSE.equals(product.getIsAvailable())) {
            throw new ValidationException("Product " + product.getName() + " is not available");
        }
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Not enough stock for: " + product.getName());
        }
    }

    private void isCustomerOrderOwner(long customerId, long existingCustomerId) {
        if (customerId != existingCustomerId) {
            throw new AccessDeniedException("Customer with id " + customerId + " can't change this order");
        }
    }

    private EntityNotFoundException orderNotFound(UUID orderId) {
        return new EntityNotFoundException(ORDER_WITH_ID + orderId + " not exists");
    }
}