package com.gamerstore.cart_service.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.gamerstore.cart_service.entity.Cart;

public interface CartRepository extends CrudRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
}
