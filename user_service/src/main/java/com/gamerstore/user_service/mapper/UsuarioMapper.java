package com.gamerstore.user_service.mapper;

import org.springframework.stereotype.Component;

import com.gamerstore.user_service.dto.UsuarioRequest;
import com.gamerstore.user_service.dto.UsuarioResponse;
import com.gamerstore.user_service.entity.Usuario;

@Component
public class UsuarioMapper {

    // DTO → Entidade
    public Usuario toEntity(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setAdmin(request.isAdmin());
        return usuario;
    }

    // Entidade → DTO
    public UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setStripeCustomerId(usuario.getStripeCustomerId()); 
        return response;
    }

    // Atualizar entidade com dados do request (sem alterar ID)
    public void updateEntity(Usuario usuario, UsuarioRequest request) {
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        if (request.getSenha() != null && !request.getSenha().isEmpty()) {
            usuario.setSenha(request.getSenha());
        }
        usuario.setAdmin(request.isAdmin());
    }
}