package com.gamerstore.shipping_service.services;

import com.gamerstore.shared.shipping.dto.ShippingOptionDTO;
import com.gamerstore.shared.shipping.dto.ShippingResponseDTO;
import com.gamerstore.shipping_service.domain.FretePorEstado;
import com.gamerstore.shipping_service.utils.CepUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private CepUtils cepUtils;

    @InjectMocks
    private ShippingService shippingService;

    private final String CEP = "01001000";

    @Test
    void deveCalcularFreteQuandoUfExiste() {

        when(cepUtils.obterUfPorCep(CEP)).thenReturn("SP");

        ShippingResponseDTO response = shippingService.calcularFrete(CEP);

        assertNotNull(response);
        assertEquals(CEP, response.cep());
        assertEquals(2, response.opcoes().size());

        ShippingOptionDTO pac = response.opcoes().get(0);
        ShippingOptionDTO sedex = response.opcoes().get(1);

        assertEquals("PAC", pac.tipo());
        assertEquals(7, pac.prazoDias());
        assertEquals(FretePorEstado.PAC.get("SP"), pac.valor());

        assertEquals("SEDEX", sedex.tipo());
        assertEquals(3, sedex.prazoDias());
        assertEquals(FretePorEstado.SEDEX.get("SP"), sedex.valor());
    }

    @Test
    void deveUsarOutrosQuandoUfNaoExiste() {

        when(cepUtils.obterUfPorCep(CEP)).thenReturn("XX");

        ShippingResponseDTO response = shippingService.calcularFrete(CEP);

        ShippingOptionDTO pac = response.opcoes().get(0);
        ShippingOptionDTO sedex = response.opcoes().get(1);

        assertEquals(FretePorEstado.PAC.get("OUTROS"), pac.valor());
        assertEquals(FretePorEstado.SEDEX.get("OUTROS"), sedex.valor());
    }

    @Test
    void devePropagarExcecaoQuandoCepUtilsFalhar() {

        when(cepUtils.obterUfPorCep(CEP))
                .thenThrow(new IllegalArgumentException("CEP inválido"));

        assertThrows(IllegalArgumentException.class,
                () -> shippingService.calcularFrete(CEP));
    }

    @Test
    void deveManterOrdemDasOpcoes() {

        when(cepUtils.obterUfPorCep(CEP)).thenReturn("SP");

        ShippingResponseDTO response = shippingService.calcularFrete(CEP);

        assertEquals("PAC", response.opcoes().get(0).tipo());
        assertEquals("SEDEX", response.opcoes().get(1).tipo());
    }

    @Test
    void deveChamarCepUtilsUmaUnicaVez() {

        when(cepUtils.obterUfPorCep(CEP)).thenReturn("SP");

        shippingService.calcularFrete(CEP);

        verify(cepUtils, times(1)).obterUfPorCep(CEP);
    }
}
