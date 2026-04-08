package com.gamerstore.user_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gamerstore.user_service.entity.Cartao;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {

    List<Cartao> findByUsuarioId(Long usuarioId);

    boolean existsByStripePaymentMethodId(String stripePaymentMethodId);
}