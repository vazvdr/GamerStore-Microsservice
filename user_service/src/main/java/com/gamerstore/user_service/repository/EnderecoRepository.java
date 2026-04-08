package com.gamerstore.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gamerstore.user_service.entity.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    // 🔎 Buscar todos os endereços de um usuário
    List<Endereco> findByUsuarioId(Long userId);
    // ⭐ Buscar endereço principal do usuário
    Optional<Endereco> findByUsuarioIdAndPrincipalTrue(Long userId);
    // ❌ Verificar se já existe endereço principal
    boolean existsByUsuarioIdAndPrincipalTrue(Long userId);
}
