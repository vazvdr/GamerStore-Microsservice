package com.gamerstore.email_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String EMAIL_QUEUE = "email.notification.queue";
    public static final String PAYMENT_CONFIRMED_ROUTING_KEY = "payment.confirmed";

    // ===================== EXCHANGE =====================
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    // ===================== FILA =====================
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    // ===================== BINDING =====================
    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(paymentExchange)
                .with(PAYMENT_CONFIRMED_ROUTING_KEY);
    }

    // ===================== CONVERSOR =====================
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}