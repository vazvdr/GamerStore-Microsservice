package com.gamerstore.product_service.messaging;

import com.gamerstore.product_service.services.ProductService;
import com.gamerstore.shared.messaging.dto.PaymentFailedEvent;
import com.gamerstore.shared.messaging.dto.ItemDTO;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockReleaseConsumer {

    private final ProductService productService;

    public StockReleaseConsumer(ProductService productService) {
        this.productService = productService;
    }

    @RabbitListener(queues = "product.stock.release.queue")
    public void releaseStock(PaymentFailedEvent event) {

        for (ItemDTO item : event.getItems()) {
            // Gera um eventId único para controle de idempotência
            String eventId = "release-" + event.getUserId() + "-" + item.getProductId() + "-" + System.currentTimeMillis();

            productService.releaseReservation(
                    item.getProductId(),
                    item.getQuantity(),
                    eventId
            );
        }
    }
}