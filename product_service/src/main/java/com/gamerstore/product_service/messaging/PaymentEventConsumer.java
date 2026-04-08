package com.gamerstore.product_service.messaging;

import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    @RabbitListener(queues = "product.payment.confirmed.queue")
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {

        if (event.getItems() == null || event.getItems().isEmpty()) {
            return;
        }
        event.getItems();
    }
}