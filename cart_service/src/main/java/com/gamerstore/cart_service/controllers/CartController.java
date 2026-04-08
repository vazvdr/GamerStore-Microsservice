package com.gamerstore.cart_service.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gamerstore.cart_service.dto.CartResponseDTO;
import com.gamerstore.cart_service.dto.CartSyncItemDTO;
import com.gamerstore.cart_service.services.CartService;
import com.gamerstore.shared.shipping.dto.ApplyShippingRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Endpoints para gerenciar o carrinho de compras")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @Operation(summary = "Sincroniza carrinho do localStorage com o Redis após login")
    @PostMapping("/{userId}/sync")
    public ResponseEntity<CartResponseDTO> syncCart(
            @PathVariable String userId,
            @RequestBody List<CartSyncItemDTO> items) {

        return ResponseEntity.ok(service.syncCart(userId, items));
    }

    @Operation(summary = "Adiciona um item ao carrinho")
    @PostMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> addItem(
            @PathVariable String userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(service.addItem(userId, productId));
    }

    @Operation(summary = "Aumenta a quantidade de um item no carrinho")
    @PatchMapping("/{userId}/items/{productId}/increase")
    public ResponseEntity<CartResponseDTO> increaseQuantity(
            @PathVariable String userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(service.increaseQuantity(userId, productId));
    }

    @Operation(summary = "Diminui a quantidade de um item no carrinho")
    @PatchMapping("/{userId}/items/{productId}/decrease")
    public ResponseEntity<CartResponseDTO> decreaseQuantity(
            @PathVariable String userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(service.decreaseQuantity(userId, productId));
    }

    @Operation(summary = "Remove um item do carrinho")
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @PathVariable String userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(service.removeItem(userId, productId));
    }

    @Operation(summary = "Retorna o carrinho do usuário")
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(
            @PathVariable String userId) {

        return ResponseEntity.ok(service.getCart(userId));
    }

    @Operation(summary = "Limpa todo o carrinho do usuário")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(
            @PathVariable String userId) {

        service.clearCart(userId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Aplica frete ao carrinho")
    @PostMapping("/{userId}/apply-shipping")
    public ResponseEntity<CartResponseDTO> applyShipping(
            @PathVariable String userId,
            @RequestBody ApplyShippingRequestDTO request) {

        return ResponseEntity.ok(
                service.applyShipping(userId, request));
    }
}