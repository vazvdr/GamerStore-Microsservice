package com.gamerstore.user_service.mapper;

import org.springframework.stereotype.Component;

import com.gamerstore.user_service.dto.EnderecoRequestDTO;
import com.gamerstore.user_service.dto.EnderecoResponseDTO;
import com.gamerstore.user_service.entity.Endereco;

@Component
public class EnderecoMapper {

    // DTO → Entidade
    public Endereco toEntity(EnderecoRequestDTO request) {
        Endereco endereco = new Endereco();
        endereco.setCep(request.getCep());
        endereco.setRua(request.getRua());
        endereco.setNumero(request.getNumero());
        endereco.setComplemento(request.getComplemento());
        endereco.setBairro(request.getBairro());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());
        endereco.setPrincipal(request.isPrincipal());
        return endereco;
    }

    // Entidade → DTO
    public EnderecoResponseDTO toResponse(Endereco endereco) {
        EnderecoResponseDTO response = new EnderecoResponseDTO();
        response.setId(endereco.getId());
        response.setCep(endereco.getCep());
        response.setRua(endereco.getRua());
        response.setNumero(endereco.getNumero());
        response.setComplemento(endereco.getComplemento());
        response.setBairro(endereco.getBairro());
        response.setCidade(endereco.getCidade());
        response.setEstado(endereco.getEstado());
        response.setPrincipal(endereco.isPrincipal());
        return response;
    }

    // Atualizar entidade com dados do request (sem alterar ID e usuário)
    public void updateEntity(Endereco endereco, EnderecoRequestDTO request) {
        endereco.setCep(request.getCep());
        endereco.setRua(request.getRua());
        endereco.setNumero(request.getNumero());
        endereco.setComplemento(request.getComplemento());
        endereco.setBairro(request.getBairro());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());
        endereco.setPrincipal(request.isPrincipal());
    }
}
