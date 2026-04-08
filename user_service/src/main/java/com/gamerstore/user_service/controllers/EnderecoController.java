package com.gamerstore.user_service.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gamerstore.user_service.dto.EnderecoRequestDTO;
import com.gamerstore.user_service.dto.EnderecoResponseDTO;
import com.gamerstore.user_service.security.JwtUtil;
import com.gamerstore.user_service.services.EnderecoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/enderecos")
@Tag(
    name = "Endereços",
    description = "Gerenciamento de endereços do usuário autenticado"
)
@SecurityRequirement(name = "bearerAuth")
public class EnderecoController {

    private final EnderecoService enderecoService;
    private final JwtUtil jwtUtil;

    public EnderecoController(
        EnderecoService enderecoService,
        JwtUtil jwtUtil
    ) {
        this.enderecoService = enderecoService;
        this.jwtUtil = jwtUtil;
    }

    // ============================
    // CADASTRAR ENDEREÇO
    // ============================
    @PostMapping("/cadastrar")
    @Operation(
        summary = "Cadastrar endereço",
        description = "Cadastra um novo endereço para o usuário autenticado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Endereço cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = EnderecoResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    public ResponseEntity<EnderecoResponseDTO> criar(
        @Parameter(description = "Token JWT", required = true)
        @RequestHeader("Authorization") String authHeader,

        @Valid
        @org.springframework.web.bind.annotation.RequestBody
        EnderecoRequestDTO dto
    ) {

        String token = jwtUtil.extrairToken(authHeader);
        Long usuarioId = JwtUtil.extrairUsuarioId(token);

        EnderecoResponseDTO response =
            enderecoService.criarEndereco(usuarioId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar endereços",
        description = "Lista os endereços do usuário autenticado"
    )
    public ResponseEntity<List<EnderecoResponseDTO>> listar(
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = jwtUtil.extrairToken(authHeader);
        Long usuarioId = JwtUtil.extrairUsuarioId(token);

        return ResponseEntity.ok(
            enderecoService.listarEnderecos(usuarioId)
        );
    }

    @PutMapping("/editar/{id}")
    @Operation(
        summary = "Atualizar endereço",
        description = "Atualiza um endereço do usuário autenticado"
    )
    public ResponseEntity<EnderecoResponseDTO> atualizar(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable Long id,
        @Valid @RequestBody EnderecoRequestDTO dto
    ) {
        String token = jwtUtil.extrairToken(authHeader);
        Long usuarioId = JwtUtil.extrairUsuarioId(token);

        return ResponseEntity.ok(
            enderecoService.atualizarEndereco(usuarioId, id, dto)
        );
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(
        summary = "Remover endereço",
        description = "Remove um endereço do usuário autenticado"
    )
    public ResponseEntity<Void> deletar(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable Long id
    ) {
        String token = jwtUtil.extrairToken(authHeader);
        Long usuarioId = JwtUtil.extrairUsuarioId(token);

        enderecoService.removerEndereco(usuarioId, id);
        return ResponseEntity.noContent().build();
    }
}
