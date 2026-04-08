package com.gamerstore.user_service.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamerstore.user_service.dto.EsqueceuSenhaRequest;
import com.gamerstore.user_service.dto.LoginRequest;
import com.gamerstore.user_service.dto.RecuperarSenhaRequest;
import com.gamerstore.user_service.dto.UsuarioRequest;
import com.gamerstore.user_service.dto.UsuarioResponse;
import com.gamerstore.user_service.security.JwtUtil;
import com.gamerstore.user_service.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/cadastrar")
    @Operation(summary = "Cadastrar novo usuário", description = "Cria um novo usuário no sistema GamerStore.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.cadastrarUsuario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Autentica o usuário e retorna um token JWT válido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UsuarioResponse usuario = usuarioService.logar(
                    loginRequest.getEmail(),
                    loginRequest.getSenha()
            );

            if (usuario != null) {
                String token = JwtUtil.gerarToken(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getStripeCustomerId()
                );

                Map<String, Object> resposta = new HashMap<>();
                resposta.put("token", token);
                resposta.put("usuario", usuario);

                return ResponseEntity.ok(resposta);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciais inválidas");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar login");
        }
    }

    @PutMapping("/editar")
    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações do usuário autenticado.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado", content = @Content)
    })
    public ResponseEntity<UsuarioResponse> atualizar(
            @Parameter(description = "Token JWT gerado após o login", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UsuarioRequest request) {

        String token = jwtUtil.extrairToken(authHeader);
        Long usuarioId = JwtUtil.extrairUsuarioId(token);
        String nomeUsuario = JwtUtil.extrairNomeUsuario(token);

        System.out.println("🔹 Usuário autenticado: " + nomeUsuario + " (ID: " + usuarioId + ")");

        UsuarioResponse response = usuarioService.atualizarUsuario(usuarioId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deletar")
    @Operation(summary = "Deletar usuário", description = "Remove o usuário autenticado do sistema.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado", content = @Content)
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Token JWT gerado após o login", required = true)
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extrairToken(authHeader);
        Long usuarioId = JwtUtil.extrairUsuarioId(token);
        String nomeUsuario = JwtUtil.extrairNomeUsuario(token);

        System.out.println("🗑️ Solicitando exclusão da conta do usuário: " + nomeUsuario + " (ID: " + usuarioId + ")");

        usuarioService.deletarUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/esqueceu-senha")
    @Operation(
        summary = "Solicitar recuperação de senha",
        description = "Gera um token temporário e envia o link de recuperação para o email do usuário."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de recuperação enviado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<?> esqueceuSenha(
            @Valid @RequestBody EsqueceuSenhaRequest request
    ) {
        usuarioService.esqueceuSenha(request.getEmail());
        return ResponseEntity.ok("Email de recuperação enviado com sucesso");
    }

    @PostMapping("/recuperar-senha")
    @Operation(
        summary = "Recuperar senha",
        description = "Valida o token temporário e atualiza a senha do usuário."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Token inválido ou expirado", content = @Content)
    })
    public ResponseEntity<?> recuperarSenha(
            @Valid @RequestBody RecuperarSenhaRequest request
    ) {
        usuarioService.recuperarSenha(
                request.getToken(),
                request.getNovaSenha()
        );
        return ResponseEntity.ok("Senha atualizada com sucesso");
    }
}
