package com.gamerstore.order_service.repositories;

import com.gamerstore.order_service.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByUserId(Long userId);

}