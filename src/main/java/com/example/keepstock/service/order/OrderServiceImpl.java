package com.example.keepstock.service.order;

import com.example.keepstock.dto.mappers.OrderMapper;
import com.example.keepstock.dto.mappers.ProductMapper;
import com.example.keepstock.dto.order.OrderDto;
import com.example.keepstock.dto.product.OrderProductResponse;
import com.example.keepstock.dto.product.ProductDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public OrderDto save(OrderDto dto) {
        if (!customerRepository.existsById(dto.getCustomer().getId())) {
            throw new EntityNotFoundException("Customer with id " + dto.getCustomer().getId() + " not already exists");
        }
        Order order = orderMapper.toEntity(dto);

        List<UUID> productIds = order.getProducts().stream().map(Product::getId).toList();
        Map<UUID, Product> products = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));


        for (ProductDto productDto : dto.getProducts()) {
            Product product = products.get(productDto.getId());

            validateProductAvailability(product, productDto.getQuantity());

            product.setQuantity(product.getQuantity() - productDto.getQuantity());

            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setId(new OrderedProductKey(order.getId(), product.getId()));
            orderedProduct.setProduct(product);
            orderedProduct.setOrder(order);
            orderedProduct.setPriceAtOrderTime(product.getPrice());
            orderedProduct.setQuantity(productDto.getQuantity());

            order.getOrderedProducts().add(orderedProduct);
        }
        order.setStatus(OrderStatus.CREATED);
        Order saveOrder = orderRepository.save(order);
        return orderMapper.toOrderDto(saveOrder);
    }

    @Override
    public OrderDto update(OrderDto dto) {
        Order existingOrder = orderRepository.findById(dto.getId())
                .orElseThrow(() -> orderNotFound(dto.getId()));

        existCustomer(dto.getCustomer().getId(), existingOrder.getCustomer().getId());

        if (existingOrder.getStatus() != OrderStatus.CREATED) {
            throw new ValidationException("Order with id " + dto.getId() + " is not created");
        }

        Set<UUID> allProductIds = Stream.concat(
                dto.getProducts().stream().map(ProductDto::getId),
                existingOrder.getOrderedProducts().stream().map(op -> op.getProduct().getId())
        ).collect(Collectors.toSet());

        Map<UUID, Product> products = productRepository.findAllById(allProductIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        Map<UUID, Long> newOrderProducts = dto.getProducts().stream()
                .collect(Collectors.toMap(ProductDto::getId, ProductDto::getQuantity, Long::sum));

        Set<UUID> processedProductIds = new HashSet<>();
        for (OrderedProduct orderedProduct : existingOrder.getOrderedProducts()) {
            UUID productId = orderedProduct.getProduct().getId();
            if (newOrderProducts.containsKey(productId)) {
                Product product = products.get(productId);
                long newQuantity = newOrderProducts.get(productId);
                if (product.getQuantity() < newQuantity) {
                    throw new InsufficientStockException("Not enough stock for: " + product.getName());
                }
                orderedProduct.setQuantity(orderedProduct.getQuantity() + newQuantity);
                orderedProduct.setPriceAtOrderTime(product.getPrice());
                product.setQuantity(product.getQuantity() - newQuantity);
                processedProductIds.add(productId);
            }
        }

        for (Map.Entry<UUID, Long> entry : newOrderProducts.entrySet()) {
            UUID productId = entry.getKey();
            if (processedProductIds.contains(productId)) continue;

            Product product = products.get(productId);

            validateProductAvailability(product, entry.getValue());

            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setId(new OrderedProductKey(existingOrder.getId(), productId));
            orderedProduct.setProduct(product);
            orderedProduct.setOrder(existingOrder);
            orderedProduct.setPriceAtOrderTime(product.getPrice());
            orderedProduct.setQuantity(entry.getValue());

            product.setQuantity(product.getQuantity() - entry.getValue());
            existingOrder.getOrderedProducts().add(orderedProduct);
        }

        return orderMapper.toOrderDto(existingOrder);
    }

    @Override
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    @Override
    public OrderDto getById(UUID id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> orderNotFound(id));
        return getOrderDto(id, existingOrder);
    }

    @Override
    public OrderDto getCustomerOrder(UUID orderId, Long customerId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> orderNotFound(orderId));
        existCustomer(existingOrder.getCustomer().getId(), customerId);
        return getOrderDto(orderId, existingOrder);
    }

    private OrderDto getOrderDto(UUID orderId, Order existingOrder) {
        List<OrderProductResponse> products = orderRepository.findOrderProductByOrderId(orderId);
        OrderDto orderDto = orderMapper.toOrderDto(existingOrder);
        orderDto.setProducts(products.stream()
                .map(productMapper::toProductDtoFromOrderProductResponse).collect(Collectors.toSet()));

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

    private void existCustomer(long customerId, long existingCustomerId) {
        if (customerId != existingCustomerId) {
            throw new AccessDeniedException("Customer with id " + customerId + " can't change this order");
        }
    }

    private EntityNotFoundException orderNotFound(UUID orderId) {
        return new EntityNotFoundException("Order with id " + orderId + " not exists");
    }
}