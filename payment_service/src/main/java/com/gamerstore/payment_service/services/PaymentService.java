package com.gamerstore.payment_service.services;

import com.gamerstore.payment_service.dto.PaymentRequestDTO;
import com.gamerstore.payment_service.messaging.PaymentEventPublisher;
import com.gamerstore.payment_service.clients.ProductClient;
import com.gamerstore.payment_service.clients.ProductResponse;

import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;
import com.gamerstore.shared.messaging.dto.PaymentFailedEvent;
import com.gamerstore.shared.messaging.dto.StockReserveEvent;
import com.gamerstore.shared.messaging.dto.ItemDTO;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    private final PaymentEventPublisher paymentEventPublisher;
    private final ProductClient productClient;

    public PaymentService(
            PaymentEventPublisher paymentEventPublisher,
            ProductClient productClient) {
        this.paymentEventPublisher = paymentEventPublisher;
        this.productClient = productClient;
    }

    public void processPayment(PaymentRequestDTO request) throws StripeException {

        com.stripe.Stripe.apiKey = stripeSecretKey;

        System.out.println("==== PAYMENT REQUEST RECEBIDO ====");
        System.out.println("USER ID: " + request.getUserId());
        System.out.println("EMAIL: " + request.getUserEmail());
        System.out.println("NAME: " + request.getUserName());
        System.out.println("AMOUNT: " + request.getAmount());
        System.out.println("=================================");

        List<ItemDTO> items = request.getItems();

        // Pega os dados que o frontend não envia por segurança
        List<ItemDTO> enrichedItems = items.stream().map(item -> {

            ProductResponse product = getProductWithResilience(item.getProductId()).join();

            ItemDTO enriched = new ItemDTO();
            enriched.setProductId(product.getId());
            enriched.setName(product.getName());
            enriched.setImageUrl(product.getImageUrl());
            enriched.setPrice(product.getPrice());
            enriched.setQuantity(item.getQuantity());

            return enriched;

        }).toList();

        // Reserva de estoque antes de aprovar
        StockReserveEvent reserveEvent = new StockReserveEvent(request.getUserId(), enrichedItems);
        paymentEventPublisher.publishStockReserve(reserveEvent);

        long amountInCents = request.getAmount()
                .multiply(new BigDecimal("100"))
                .longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("brl")
                .setCustomer(request.getStripeCustomerId())
                .setPaymentMethod(request.getPaymentMethodId())
                .setConfirm(true)
                .setOffSession(true)
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        if ("succeeded".equals(intent.getStatus())) {

            BigDecimal subtotal = enrichedItems.stream()
                    .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal total = request.getAmount();

            PaymentConfirmedEvent event = new PaymentConfirmedEvent(
                    request.getUserId(),
                    intent.getId(),
                    request.getUserEmail(),
                    request.getUserName(),
                    total,
                    subtotal,
                    BigDecimal.ZERO,
                    enrichedItems);

            paymentEventPublisher.publishPaymentConfirmed(event);

        } else {
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    request.getUserId(),
                    enrichedItems);

            paymentEventPublisher.publishPaymentFailed(failedEvent);
        }
    }

    // Resilience4J
    @Retry(name = "productService")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackProduct")
    @TimeLimiter(name = "productService")
    @Bulkhead(name = "productService")
    public CompletableFuture<ProductResponse> getProductWithResilience(Long productId) {
        return CompletableFuture.supplyAsync(() -> productClient.getProductById(productId));
    }

    // Fallback
    public CompletableFuture<ProductResponse> fallbackProduct(Long productId, Throwable ex) {

        System.out.println("🔥 Falha ao buscar produto: " + ex.getMessage());

        throw new RuntimeException("Erro ao validar produto no momento");
    }
}