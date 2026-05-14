package com.gamerstore.suggestion_service.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
                objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(
                connectionFactory);

        template.setKeySerializer(
                new StringRedisSerializer());

        template.setHashKeySerializer(
                new StringRedisSerializer());

        template.setValueSerializer(
                serializer);

        template.setHashValueSerializer(
                serializer);

        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
                objectMapper);

        // TTL padrão
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer))
                .disableCachingNullValues()
                .entryTtl(
                        Duration.ofMinutes(10));

        // Cache específico para sugestões
        RedisCacheConfiguration suggestionsConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer))
                .disableCachingNullValues()
                .entryTtl(
                        Duration.ofHours(1));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(
                "suggestions",
                suggestionsConfig);

        return RedisCacheManager.builder(
                connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(
                        cacheConfigurations)
                .build();
    }
}