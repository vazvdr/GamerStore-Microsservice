package com.gamerstore.suggestion_service.controllers;

import com.gamerstore.shared.suggestion.dto.SuggestionProductDTO;
import com.gamerstore.suggestion_service.dto.ViewRequestDTO;
import com.gamerstore.suggestion_service.services.SuggestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suggestions")
@Tag(name = "Suggestions", description = "Endpoints responsáveis pelas sugestões de produtos")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(
            SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @Operation(summary = "Registrar visualização de produto", description = """
            Registra a visualização de um produto por um usuário.
            Essas informações são utilizadas para gerar sugestões inteligentes.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visualização registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados")
    })
    @PostMapping(value = "/view", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> registerView(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da visualização", required = true, content = @Content(schema = @Schema(implementation = ViewRequestDTO.class), examples = @ExampleObject(value = """
                    {
                      "userId": 1,
                      "productId": 10
                    }
                    """)))

            @RequestBody ViewRequestDTO dto) {

        suggestionService.registerView(dto);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Buscar sugestões de produtos", description = """
            Retorna uma lista de produtos sugeridos
            com base no comportamento de usuários similares.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugestões retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SuggestionProductDTO>> suggestProducts(

            @Parameter(description = "ID do produto para gerar sugestões", example = "10")

            @PathVariable Long productId) {

        return ResponseEntity.ok(
                suggestionService
                        .suggestProducts(productId));
    }
}