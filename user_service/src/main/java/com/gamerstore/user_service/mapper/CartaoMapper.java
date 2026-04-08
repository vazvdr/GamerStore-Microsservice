package com.gamerstore.user_service.mapper;

import org.springframework.stereotype.Component;
import com.gamerstore.user_service.dto.CartaoResponse;
import com.gamerstore.user_service.entity.Cartao;

@Component
public class CartaoMapper {

    public CartaoResponse toResponse(Cartao pagamento) {
        CartaoResponse response = new CartaoResponse();
        response.setId(pagamento.getId());
        response.setBrand(pagamento.getBrand());
        response.setLast4(pagamento.getLast4());
        response.setExpMonth(pagamento.getExpMonth());
        response.setExpYear(pagamento.getExpYear());
        response.setStripePaymentMethodId(pagamento.getStripePaymentMethodId());
        return response;
    }
}