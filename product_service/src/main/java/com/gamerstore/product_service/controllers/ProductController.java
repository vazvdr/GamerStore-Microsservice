package com.gamerstore.product_service.controllers;

import com.gamerstore.product_service.dto.ProductResponseDTO;
import com.gamerstore.product_service.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Endpoints para gerenciamento de produtos")
public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        @Operation(summary = "Listar todos os produtos")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
        })
        @GetMapping
        public ResponseEntity<Page<ProductResponseDTO>> findAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "8") int size) {

                return ResponseEntity.ok(
                                productService.findAll(page, size));
        }

        @Operation(summary = "Buscar produto por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
                        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
                ProductResponseDTO product = productService.findById(id);

                return ResponseEntity.ok()
                                .header("Cache-Control", "public, max-age=600, s-maxage=1200")
                                .body(product);
        }

        @Operation(summary = "Busca geral de produtos", description = """
                        Realiza uma busca geral e normalizada por:
                        - Nome
                        - Descrição
                        - Marca
                        - Modelo
                        - Categoria (tags)
                        - Preço

                        A busca ignora:
                        - Maiúsculas/minúsculas
                        - Acentos
                        - Espaços e caracteres especiais
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
                        @ApiResponse(responseCode = "204", description = "Nenhum produto encontrado")
        })
        @GetMapping("/search")
        public List<ProductResponseDTO> search(
                        @Parameter(description = "Termo livre para busca (ex: notebook, notebooks, placa de video, RTX 4060, etc)", required = true, example = "notebooks") @RequestParam("q") String query) {
                return productService.search(query);
        }

        @Operation(summary = "Buscar múltiplos produtos por IDs", description = "Recebe uma lista de IDs e retorna os produtos correspondentes com todos os campos, incluindo estoque (stock)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Produtos retornados com sucesso"),
                        @ApiResponse(responseCode = "204", description = "Nenhum produto encontrado")
        })
        @PostMapping("/batch")
        public List<ProductResponseDTO> getProductsBatch(
                        @Parameter(description = "Lista de IDs dos produtos", required = true, example = "[1,2,3,15]") @RequestBody List<Long> ids) {
                return productService.getProductsByIds(ids);
        }
}