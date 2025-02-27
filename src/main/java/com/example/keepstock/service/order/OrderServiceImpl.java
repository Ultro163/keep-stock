package com.example.keepstock.service.order;

import com.example.keepstock.dto.mappers.OrderMapper;
import com.example.keepstock.dto.order.OrderDto;
import com.example.keepstock.entity.Order;
import com.example.keepstock.entity.OrderedProduct;
import com.example.keepstock.entity.OrderedProductKey;
import com.example.keepstock.entity.Product;
import com.example.keepstock.error.exception.InsufficientStockException;
import com.example.keepstock.error.exception.ValidationException;
import com.example.keepstock.model.OrderStatus;
import com.example.keepstock.repository.OrderRepository;
import com.example.keepstock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto save(OrderDto dto) {
        Order order = orderMapper.toEntity(dto);

        List<UUID> productIds = order.getProducts().stream().map(Product::getId).toList();
        Map<UUID, Product> products = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        Set<OrderedProduct> orderedProducts = new HashSet<>();


        for (Product product : order.getProducts()) {
            Product origProduct = products.get(product.getId());
            if (Boolean.FALSE.equals(origProduct.getIsAvailable())) {
                throw new ValidationException("Product is not available");
            }
            if (origProduct.getQuantity() < product.getQuantity()) {
                throw new InsufficientStockException("Not enough product in stock: " + product.getName());
            }
            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setId(new OrderedProductKey());
            orderedProduct.setProduct(origProduct);
            orderedProduct.setPriceAtOrderTime(origProduct.getPrice());
            orderedProduct.setQuantity(product.getQuantity());
            orderedProduct.setOrder(order);
            orderedProducts.add(orderedProduct);
        }
        order.setOrderedProducts(orderedProducts);
        order.setStatus(OrderStatus.CREATED);
        Order saveOrder = orderRepository.save(order);
        return orderMapper.toOrderDto(saveOrder);
    }

    @Override
    public OrderDto update(OrderDto dto) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    @Override
    public OrderDto getById(UUID id) {
        return null;
    }

    @Override
    public OrderDto getCustomerOrder(UUID orderId, Long customerId) {
        return null;
    }
}