package com.gamerstore.order_service.controllers;

import com.gamerstore.order_service.models.Order;
import com.gamerstore.order_service.repositories.OrderRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Pedidos", description = "Endpoints relacionados a pedidos dos usuários")
public class OrderController {

    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @Operation(
            summary = "Buscar pedidos por usuário",
            description = "Retorna todos os pedidos realizados por um usuário específico"
    )
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Long userId) {

        return repository.findByUserId(userId);
    }
}