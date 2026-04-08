package com.gamerstore.user_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamerstore.user_service.dto.EnderecoRequestDTO;
import com.gamerstore.user_service.dto.EnderecoResponseDTO;
import com.gamerstore.user_service.entity.Endereco;
import com.gamerstore.user_service.entity.Usuario;
import com.gamerstore.user_service.mapper.EnderecoMapper;
import com.gamerstore.user_service.repository.EnderecoRepository;
import com.gamerstore.user_service.repository.UsuarioRepository;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoMapper enderecoMapper;

    public EnderecoService(
            EnderecoRepository enderecoRepository,
            UsuarioRepository usuarioRepository,
            EnderecoMapper enderecoMapper
    ) {
        this.enderecoRepository = enderecoRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoMapper = enderecoMapper;
    }

    // 📌 Criar endereço
    @Transactional
    public EnderecoResponseDTO criarEndereco(Long userId, EnderecoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Garante apenas um endereço principal
        if (request.isPrincipal()) {
            enderecoRepository
                    .findByUsuarioIdAndPrincipalTrue(userId)
                    .ifPresent(end -> {
                        end.setPrincipal(false);
                        enderecoRepository.save(end);
                    });
        }

        Endereco endereco = enderecoMapper.toEntity(request);
        endereco.setUsuario(usuario);

        Endereco salvo = enderecoRepository.save(endereco);
        return enderecoMapper.toResponse(salvo);
    }

    // 📌 Listar endereços do usuário
    public List<EnderecoResponseDTO> listarEnderecos(Long userId) {
        return enderecoRepository.findByUsuarioId(userId)
                .stream()
                .map(enderecoMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 📌 Atualizar endereço
    @Transactional
    public EnderecoResponseDTO atualizarEndereco(
            Long userId,
            Long enderecoId,
            EnderecoRequestDTO request
    ) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        // Segurança: endereço pertence ao usuário?
        if (!endereco.getUsuario().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }

        if (request.isPrincipal()) {
            enderecoRepository
                    .findByUsuarioIdAndPrincipalTrue(userId)
                    .ifPresent(end -> {
                        if (!end.getId().equals(enderecoId)) {
                            end.setPrincipal(false);
                            enderecoRepository.save(end);
                        }
                    });
        }

        enderecoMapper.updateEntity(endereco, request);
        Endereco atualizado = enderecoRepository.save(endereco);

        return enderecoMapper.toResponse(atualizado);
    }

    // 📌 Remover endereço
    @Transactional
    public void removerEndereco(Long userId, Long enderecoId) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        if (!endereco.getUsuario().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }

        enderecoRepository.delete(endereco);
    }
}
