package com.gamerstore.order_service.messaging;

import com.gamerstore.order_service.config.RabbitMQConfig;
import com.gamerstore.order_service.services.OrderService;
import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfirmedConsumer {

    private final OrderService orderService;

    public PaymentConfirmedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_CONFIRMED_QUEUE)
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
        orderService.createOrder(event);
    }
}