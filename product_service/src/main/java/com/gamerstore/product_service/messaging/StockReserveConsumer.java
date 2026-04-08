package com.gamerstore.product_service.messaging;

import com.gamerstore.product_service.services.ProductService;
import com.gamerstore.shared.messaging.dto.StockReserveEvent;
import com.gamerstore.shared.messaging.dto.ItemDTO;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockReserveConsumer {

    private final ProductService productService;

    public StockReserveConsumer(ProductService productService) {
        this.productService = productService;
    }

    @RabbitListener(queues = "product.stock.reserve.queue")
    public void reserveStock(StockReserveEvent event) {

        for (ItemDTO item : event.getItems()) {
            // Gera um eventId único para controle de idempotência
            String eventId = "reserve-" + event.getUserId() + "-" + item.getProductId() + "-" + System.currentTimeMillis();

            productService.reserveStock(
                    item.getProductId(),
                    item.getQuantity(),
                    eventId
            );
        }
    }
}