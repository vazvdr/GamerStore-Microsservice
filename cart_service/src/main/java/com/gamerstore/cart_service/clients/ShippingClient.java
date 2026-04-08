package com.gamerstore.cart_service.clients;

import com.gamerstore.shared.shipping.dto.ShippingRequestDTO;
import com.gamerstore.shared.shipping.dto.ShippingResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shipping-service", url = "${SHIPPING_SERVICE_URL}")
public interface ShippingClient {

    @PostMapping("/shipping/calcular")
    ShippingResponseDTO calcularFrete(
        @RequestBody ShippingRequestDTO request
    );
}
