package com.gamerstore.suggestion_service.services;

import com.gamerstore.shared.suggestion.dto.SuggestionProductDTO;
import com.gamerstore.suggestion_service.clients.ProductClient;
import com.gamerstore.suggestion_service.dto.ViewRequestDTO;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SuggestionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ProductClient productClient;

    public SuggestionService(
            RedisTemplate<String, Object> redisTemplate,
            ProductClient productClient
    ) {
        this.redisTemplate = redisTemplate;
        this.productClient = productClient;
    }

    public void registerView(ViewRequestDTO dto) {

        String userKey =
                "user:" + dto.userId() + ":views";

        String productKey =
                "product:" + dto.productId() + ":users";

        redisTemplate.opsForSet()
                .add(userKey, dto.productId());

        redisTemplate.opsForSet()
                .add(productKey, dto.userId());

        redisTemplate.expire(
                userKey,
                30,
                TimeUnit.DAYS
        );

        redisTemplate.expire(
                productKey,
                30,
                TimeUnit.DAYS
        );
    }

    @Cacheable(
            value = "suggestions",
            key = "#productId"
    )
    public List<SuggestionProductDTO>
    suggestProducts(Long productId) {

        String productKey =
                "product:" + productId + ":users";

        Set<Object> users =
                redisTemplate.opsForSet()
                        .members(productKey);

        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Integer> frequencyMap =
                new HashMap<>();

        for (Object user : users) {

            String userKey =
                    "user:" + user + ":views";

            Set<Object> viewedProducts =
                    redisTemplate.opsForSet()
                            .members(userKey);

            if (viewedProducts == null) {
                continue;
            }

            for (Object viewed : viewedProducts) {

                Long viewedId =
                        Long.valueOf(viewed.toString());

                if (!viewedId.equals(productId)) {

                    frequencyMap.merge(
                            viewedId,
                            1,
                            Integer::sum
                    );
                }
            }
        }

        return frequencyMap.entrySet()
                .stream()
                .sorted((a, b) ->
                        b.getValue() - a.getValue())
                .limit(5)
                .map(entry ->
                        productClient.findById(
                                entry.getKey()))
                .toList();
    }
}