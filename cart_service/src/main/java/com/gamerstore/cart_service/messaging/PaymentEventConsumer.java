package com.gamerstore.cart_service.messaging;

import com.gamerstore.cart_service.services.CartService;
import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private final CartService cartService;

    public PaymentEventConsumer(CartService cartService) {
        this.cartService = cartService;
    }

    // Escuta os eventos da exchange "payment.exchange" com a routing key
    @RabbitListener(queues = "cart.payment.confirmed.queue")
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {

        // converte Long para String
        cartService.clearCart(event.getUserId().toString());

    }
}