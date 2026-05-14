package com.gamerstore.email_service.services;

import com.gamerstore.shared.messaging.dto.ItemDTO;
import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({
        "unchecked",
        "null"
})
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {

        ReflectionTestUtils.setField(
                emailService,
                "brevoApiUrl",
                "https://api.brevo.com/v3/smtp/email");

        ReflectionTestUtils.setField(
                emailService,
                "brevoApiKey",
                "fake-api-key");

        ReflectionTestUtils.setField(
                emailService,
                "fromEmail",
                "noreply@gamerstore.com");

        ReflectionTestUtils.setField(
                emailService,
                "fromName",
                "GamerStore");

        ReflectionTestUtils.setField(
                emailService,
                "restTemplate",
                restTemplate);
    }

    @Test
    void enviarEmailPedidoConfirmado_shouldSendEmailSuccessfully() {

        PaymentConfirmedEvent event = buildEvent();

        when(
                restTemplate.postForEntity(
                        anyString(),
                        any(),
                        eq(String.class)))
                .thenReturn(
                        ResponseEntity.ok("Email enviado"));

        assertDoesNotThrow(() -> emailService.enviarEmailPedidoConfirmado(event));

        verify(restTemplate, times(1))
                .postForEntity(
                        anyString(),
                        any(),
                        eq(String.class));
    }

    @Test
    void enviarEmailPedidoConfirmado_shouldThrowExceptionWhenBrevoFails() {

        PaymentConfirmedEvent event = buildEvent();

        when(
                restTemplate.postForEntity(
                        anyString(),
                        any(),
                        eq(String.class)))
                .thenReturn(
                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Erro"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailService
                        .enviarEmailPedidoConfirmado(event));

        assertEquals(
                "Erro ao enviar email",
                exception.getMessage());
    }

    @Test
    void enviarEmailPedidoConfirmado_shouldContainCorrectHeaders() {

        PaymentConfirmedEvent event = buildEvent();

        when(
                restTemplate.postForEntity(
                        anyString(),
                        any(),
                        eq(String.class)))
                .thenReturn(
                        ResponseEntity.ok("OK"));

        emailService.enviarEmailPedidoConfirmado(event);

        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor = ArgumentCaptor.forClass(
                (Class<HttpEntity<Map<String, Object>>>) (Class<?>) HttpEntity.class);

        verify(restTemplate).postForEntity(
                anyString(),
                captor.capture(),
                eq(String.class));

        HttpEntity<Map<String, Object>> entity = captor.getValue();

        assertEquals(
                "fake-api-key",
                entity.getHeaders().getFirst("api-key"));

        assertEquals(
                "application/json",
                entity.getHeaders().getContentType().toString());
    }

    @Test
    void enviarEmailPedidoConfirmado_shouldContainCorrectBody() {

        PaymentConfirmedEvent event = buildEvent();

        when(
                restTemplate.postForEntity(
                        anyString(),
                        any(),
                        eq(String.class)))
                .thenReturn(
                        ResponseEntity.ok("OK"));

        emailService.enviarEmailPedidoConfirmado(event);

        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor = ArgumentCaptor.forClass(
                (Class<HttpEntity<Map<String, Object>>>) (Class<?>) HttpEntity.class);

        verify(restTemplate).postForEntity(
                anyString(),
                captor.capture(),
                eq(String.class));

        HttpEntity<Map<String, Object>> entity = captor.getValue();

        Map<String, Object> body = entity.getBody();

        assertNotNull(body);

        assertEquals(
                "Pedido confirmado 🎮 - GamerStore",
                body.get("subject"));

        assertTrue(
                body.get("htmlContent")
                        .toString()
                        .contains("Pedido Confirmado 🎮"));

        assertTrue(
                body.get("htmlContent")
                        .toString()
                        .contains("Teclado Gamer"));
    }

    @Test
    void enviarEmailPedidoConfirmado_shouldContainCorrectRecipient() {

        PaymentConfirmedEvent event = buildEvent();

        when(
                restTemplate.postForEntity(
                        anyString(),
                        any(),
                        eq(String.class)))
                .thenReturn(
                        ResponseEntity.ok("OK"));

        emailService.enviarEmailPedidoConfirmado(event);

        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor = ArgumentCaptor.forClass(
                (Class<HttpEntity<Map<String, Object>>>) (Class<?>) HttpEntity.class);

        verify(restTemplate).postForEntity(
                anyString(),
                captor.capture(),
                eq(String.class));

        HttpEntity<Map<String, Object>> entity = captor.getValue();

        Map<String, Object> body = entity.getBody();

        assertNotNull(body);

        Object[] to = (Object[]) body.get("to");

        assertEquals(1, to.length);

        Map<String, String> recipient = (Map<String, String>) to[0];

        assertEquals(
                "john@gamerstore.com",
                recipient.get("email"));

        assertEquals(
                "John Doe",
                recipient.get("name"));
    }

    @Test
    void enviarEmailPedidoConfirmado_shouldContainCorrectSender() {

        PaymentConfirmedEvent event = buildEvent();

        when(
                restTemplate.postForEntity(
                        anyString(),
                        any(),
                        eq(String.class)))
                .thenReturn(
                        ResponseEntity.ok("OK"));

        emailService.enviarEmailPedidoConfirmado(event);

        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor = ArgumentCaptor.forClass(
                (Class<HttpEntity<Map<String, Object>>>) (Class<?>) HttpEntity.class);

        verify(restTemplate).postForEntity(
                anyString(),
                captor.capture(),
                eq(String.class));

        HttpEntity<Map<String, Object>> entity = captor.getValue();

        Map<String, Object> body = entity.getBody();

        assertNotNull(body);

        Map<String, String> sender = (Map<String, String>) body.get("sender");

        assertEquals(
                "noreply@gamerstore.com",
                sender.get("email"));

        assertEquals(
                "GamerStore",
                sender.get("name"));
    }

    private PaymentConfirmedEvent buildEvent() {

        ItemDTO item = new ItemDTO(
                1L,
                "Teclado Gamer",
                "https://image.com/teclado.png",
                BigDecimal.valueOf(299.90),
                2);

        return new PaymentConfirmedEvent(
                1L,
                "pi_123",
                "john@gamerstore.com",
                "John Doe",
                BigDecimal.valueOf(699.90),
                BigDecimal.valueOf(599.90),
                BigDecimal.valueOf(100.00),
                List.of(item));
    }
}