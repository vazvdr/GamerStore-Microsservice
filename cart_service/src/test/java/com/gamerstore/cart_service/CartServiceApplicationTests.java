package com.gamerstore.cart_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

@Disabled
@SpringBootTest
class CartServiceApplicationTests {

    @MockBean
    private RedisTemplate<?, ?> redisTemplate;

    @MockBean
    private RabbitTemplate rabbitTemplate;


	@Test
	void contextLoads() {
	}

}
