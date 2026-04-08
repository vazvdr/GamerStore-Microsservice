package com.gamerstore.user_service.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gamerstore.user_service.dto.CartaoRequest;
import com.gamerstore.user_service.dto.CartaoResponse;
import com.gamerstore.user_service.services.CartaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cartoes")
@Tag(name = "Cartões", description = "Gerenciamento de cartões do usuário")
public class CartaoController {

    private final CartaoService pagamentoService;

    public CartaoController(CartaoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    @Operation(summary = "Criar cartão", description = "Cadastra um novo cartão")
    public ResponseEntity<CartaoResponse> criar(
            @RequestBody CartaoRequest request) {

        CartaoResponse response = pagamentoService.criar(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar cartões", description = "Lista todos os cartões")
    public ResponseEntity<List<CartaoResponse>> listar() {

        List<CartaoResponse> pagamentos = pagamentoService.listar();
        return ResponseEntity.ok(pagamentos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cartão", description = "Remove um cartão pelo ID")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        pagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}