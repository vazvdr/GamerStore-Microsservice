package com.gamerstore.suggestion_service.controllers;

import com.gamerstore.shared.suggestion.dto.SuggestionProductDTO;
import com.gamerstore.suggestion_service.dto.ViewRequestDTO;
import com.gamerstore.suggestion_service.services.SuggestionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(
            SuggestionService suggestionService
    ) {
        this.suggestionService = suggestionService;
    }

    @PostMapping("/view")
    public ResponseEntity<Void> registerView(
            @RequestBody ViewRequestDTO dto
    ) {

        suggestionService.registerView(dto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SuggestionProductDTO>>
    suggestProducts(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                suggestionService
                        .suggestProducts(productId)
        );
    }
}