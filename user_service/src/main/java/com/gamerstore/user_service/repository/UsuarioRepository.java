package com.gamerstore.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamerstore.user_service.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{	
	Optional<Usuario> findByEmail(String email);
	boolean existsByEmail(String email);

}
