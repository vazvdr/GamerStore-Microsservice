package com.gamerstore.order_service.services;

import com.gamerstore.order_service.models.Order;
import com.gamerstore.order_service.models.OrderItem;
import com.gamerstore.order_service.repositories.OrderRepository;
import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public void createOrder(PaymentConfirmedEvent event) {

        List<OrderItem> items = event.getItems()
                .stream()
                .map(i -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(i.getProductId());
                    item.setQuantity(i.getQuantity());

                    item.setName(i.getName());
                    item.setImageUrl(i.getImageUrl());
                    item.setPrice(i.getPrice());

                    return item;
                })
                .collect(Collectors.toList());

        Order order = new Order(
                event.getUserId(),
                event.getPaymentIntentId(),
                event.getAmount(),     
                event.getSubtotal(),   
                event.getShipping(),  
                items
        );

        repository.save(order);

    }
}