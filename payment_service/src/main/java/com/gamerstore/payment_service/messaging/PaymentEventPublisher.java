package com.gamerstore.payment_service.messaging;

import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;
import com.gamerstore.shared.messaging.dto.PaymentFailedEvent;
import com.gamerstore.shared.messaging.dto.StockReserveEvent;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentConfirmed(PaymentConfirmedEvent event) {
        rabbitTemplate.convertAndSend(
                "payment.exchange",        
                "payment.confirmed",      
                event
        );
    }

    public void publishStockReserve(StockReserveEvent event) {

        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "stock.reserve",
                event
        );
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {

        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.failed",
                event
        );

    }
}