package com.example.keepstock.repository;

import com.example.keepstock.dto.product.OrderProductResponse;
import com.example.keepstock.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    @Query("""
            select new com.example.keepstock.dto.product.OrderProductResponse(p.id, p.name, op.priceAtOrderTime, op.quantity)
            from OrderedProduct op
            join op.product p
            join op.order o
            where o.id = :id
            """)
    List<OrderProductResponse> findOrderProductByOrderId(@Param("id") UUID id);
}