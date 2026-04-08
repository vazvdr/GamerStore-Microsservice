package com.gamerstore.payment_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    public static final String CART_CLEAR_QUEUE = "cart.clear.queue";
    public static final String PRODUCT_STOCK_QUEUE = "product.stock.queue";
    public static final String EMAIL_QUEUE = "email.notification.queue"; 

    public static final String PAYMENT_CONFIRMED_ROUTING_KEY = "payment.confirmed";

    // ===================== EXCHANGE =====================
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    // ===================== FILAS =====================
    @Bean
    public Queue cartClearQueue() {
        return new Queue(CART_CLEAR_QUEUE, true);
    }

    @Bean
    public Queue productStockQueue() {
        return new Queue(PRODUCT_STOCK_QUEUE, true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true); 
    }

    // ===================== BINDINGS =====================
    @Bean
    public Binding cartBinding(Queue cartClearQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(cartClearQueue)
                .to(paymentExchange)
                .with(PAYMENT_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding productBinding(Queue productStockQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(productStockQueue)
                .to(paymentExchange)
                .with(PAYMENT_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(paymentExchange)
                .with(PAYMENT_CONFIRMED_ROUTING_KEY);
    }

    // ===================== CONVERSOR JSON =====================
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ===================== RABBIT TEMPLATE =====================
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter()); // usa JSON
        return template;
    }
}