package com.gamerstore.product_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "product.payment.confirmed.queue";
    public static final String EXCHANGE = "payment.exchange";
    public static final String ROUTING_KEY = "payment.confirmed";

    // NOVAS FILAS DA SAGA
    public static final String STOCK_RESERVE_QUEUE = "product.stock.reserve.queue";
    public static final String PAYMENT_FAILED_QUEUE = "product.stock.release.queue";

    public static final String STOCK_RESERVE_ROUTING_KEY = "stock.reserve";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    @Bean
    public Queue paymentConfirmedQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue stockReserveQueue() {
        return new Queue(STOCK_RESERVE_QUEUE, true);
    }

    @Bean
    public Queue paymentFailedQueue() {
        return new Queue(PAYMENT_FAILED_QUEUE, true);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue paymentConfirmedQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(paymentConfirmedQueue)
                .to(paymentExchange)
                .with(ROUTING_KEY);
    }

    // binding reserva estoque
    @Bean
    public Binding stockReserveBinding(Queue stockReserveQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(stockReserveQueue)
                .to(paymentExchange)
                .with(STOCK_RESERVE_ROUTING_KEY);
    }

    // binding liberar estoque
    @Bean
    public Binding paymentFailedBinding(Queue paymentFailedQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(paymentFailedQueue)
                .to(paymentExchange)
                .with(PAYMENT_FAILED_ROUTING_KEY);
    }

    // ✅ Conversor JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ✅ RabbitTemplate usando JSON
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // ✅ Listener converter
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}