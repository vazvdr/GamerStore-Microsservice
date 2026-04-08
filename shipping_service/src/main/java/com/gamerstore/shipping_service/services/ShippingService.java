package com.gamerstore.shipping_service.services;

import com.gamerstore.shared.shipping.dto.ShippingOptionDTO;
import com.gamerstore.shipping_service.domain.FretePorEstado;
import com.gamerstore.shared.shipping.dto.ShippingResponseDTO;
import com.gamerstore.shipping_service.utils.CepUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingService {

    private final CepUtils cepUtils;

    public ShippingService(CepUtils cepUtils) {
        this.cepUtils = cepUtils;
    }

    public ShippingResponseDTO calcularFrete(String cep) {

        String uf = cepUtils.obterUfPorCep(cep);

        ShippingOptionDTO pac = new ShippingOptionDTO(
                "PAC",
                7,
                FretePorEstado.PAC.getOrDefault(uf, FretePorEstado.PAC.get("OUTROS"))
        );

        ShippingOptionDTO sedex = new ShippingOptionDTO(
                "SEDEX",
                3,
                FretePorEstado.SEDEX.getOrDefault(uf, FretePorEstado.SEDEX.get("OUTROS"))
        );

        return new ShippingResponseDTO(
                cep,
                List.of(pac, sedex)
        );
    }
}
