package com.gamerstore.payment_service.controllers;

import com.gamerstore.payment_service.dto.PaymentRequestDTO;
import com.gamerstore.payment_service.services.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
@Tag(name = "Payments", description = "Endpoints de pagamento via Stripe")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Criar pagamento", description = "Processa um pagamento usando as informações fornecidas no request")
    @ApiResponse(responseCode = "200", description = "Pagamento processado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Erro ao processar pagamento")
    @PostMapping
    public ResponseEntity<String> createPayment(
            @Valid @RequestBody PaymentRequestDTO request) {

        if (request.getItems() != null) {
            request.getItems().forEach(item -> System.out.println("Item -> productId: "
                    + item.getProductId()
                    + " quantity: "
                    + item.getQuantity()));
        }

        try {
            paymentService.processPayment(request);
            return ResponseEntity.ok("Pagamento processado");
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Erro ao processar pagamento");
        }
    }
}