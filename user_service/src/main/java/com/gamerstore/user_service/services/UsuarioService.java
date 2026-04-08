package com.gamerstore.user_service.services;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gamerstore.user_service.dto.UsuarioRequest;
import com.gamerstore.user_service.dto.UsuarioResponse;
import com.gamerstore.user_service.entity.Usuario;
import com.gamerstore.user_service.mapper.UsuarioMapper;
import com.gamerstore.user_service.repository.UsuarioRepository;
import com.gamerstore.user_service.security.JwtUtil;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.stripe.Stripe;
import com.stripe.model.Customer;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public UsuarioService(UsuarioRepository usuarioRepository, JwtUtil jwtUtil, EmailService emailService,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UsuarioResponse cadastrarUsuario(UsuarioRequest request) {

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        try {
            // 🔹 Configura Stripe
            Stripe.apiKey = stripeSecretKey;

            // 🔹 Cria Customer na Stripe
            Map<String, Object> params = new HashMap<>();
            params.put("email", request.getEmail());
            params.put("name", request.getNome());

            Customer customer = Customer.create(params);

            // 🔹 Cria entidade usuário
            Usuario usuario = usuarioMapper.toEntity(request);
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

            // 🔹 Salva stripeCustomerId
            usuario.setStripeCustomerId(customer.getId());

            // 🔹 Salva no banco
            Usuario salvo = usuarioRepository.save(usuario);

            return usuarioMapper.toResponse(salvo);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar usuário na Stripe", e);
        }
    }

    public UsuarioResponse logar(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse atualizarUsuario(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        usuarioMapper.updateEntity(usuario, request);

        if (request.getSenha() != null && !request.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(atualizado);
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public void esqueceuSenha(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 🔐 Gera JWT temporário para reset de senha
        String tokenReset = jwtUtil.generatePasswordResetToken(usuario.getId());

        // 🔗 Link de recuperação
        String link = "https://gamerstore-shop.vercel.app/recuperar-senha?token=" + tokenReset;

        // ✉️ Envio de email
        emailService.enviarEmailRecuperacaoSenha(
                usuario.getEmail(),
                usuario.getNome(),
                link);
    }

    public void recuperarSenha(String token, String novaSenha) {

        // ✅ Valida token e obtém ID do usuário
        Long usuarioId = jwtUtil.validatePasswordResetToken(token);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 🔒 Atualiza senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

}
