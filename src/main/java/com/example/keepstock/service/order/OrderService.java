package com.example.keepstock.service.order;

import com.example.keepstock.entity.Order;
import com.example.keepstock.service.CrudService;

import java.util.UUID;

public interface OrderService extends CrudService<Order, UUID> {
}
