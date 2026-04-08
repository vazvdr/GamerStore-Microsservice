package com.gamerstore.email_service.messaging;

import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;
import com.gamerstore.email_service.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private final EmailService emailService;

    public PaymentEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "email.notification.queue")
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {

        System.out.println("📧 Evento recebido no email-service: " + event.getPaymentIntentId());

        try {
            emailService.enviarEmailPedidoConfirmado(event);
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar email: " + e.getMessage());
            throw e; // importante pra retry
        }
    }
}