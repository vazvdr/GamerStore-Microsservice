package com.gamerstore.user_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gamerstore.user_service.dto.CartaoRequest;
import com.gamerstore.user_service.dto.CartaoResponse;
import com.gamerstore.user_service.entity.Cartao;
import com.gamerstore.user_service.entity.Usuario;
import com.gamerstore.user_service.mapper.CartaoMapper;
import com.gamerstore.user_service.repository.CartaoRepository;
import com.gamerstore.user_service.repository.UsuarioRepository;
import com.gamerstore.user_service.security.JwtUtil;
import com.stripe.model.PaymentMethod;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;

@Service
public class CartaoService {

    private final CartaoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CartaoMapper pagamentoMapper;
    private final JwtUtil jwtUtil;
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    // 🔥 Injeção via construtor explícito (sem Lombok)
    public CartaoService(CartaoRepository pagamentoRepository,
            UsuarioRepository usuarioRepository,
            CartaoMapper pagamentoMapper,
            JwtUtil jwtUtil) {
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pagamentoMapper = pagamentoMapper;
        this.jwtUtil = jwtUtil;
    }

    // 🔥 Método auxiliar privado para evitar repetição
    private String extrairEmailDoToken() {

        String authHeader = org.springframework.web.context.request.RequestContextHolder
                .getRequestAttributes() instanceof org.springframework.web.context.request.ServletRequestAttributes attrs
                        ? attrs.getRequest().getHeader("Authorization")
                        : null;

        String token = jwtUtil.extrairToken(authHeader);

        return JwtUtil.extrairEmail(token);
    }

    public CartaoResponse criar(CartaoRequest request) {

        String email = extrairEmailDoToken();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {

            Stripe.apiKey = stripeSecretKey;

            PaymentMethod paymentMethod = PaymentMethod.retrieve(request.getPaymentMethodId());

            if (pagamentoRepository.existsByStripePaymentMethodId(paymentMethod.getId())) {
                throw new RuntimeException("Cartão já cadastrado");
            }

            paymentMethod.attach(
                    Map.of("customer", usuario.getStripeCustomerId()));

            PaymentMethod atualizado = PaymentMethod.retrieve(request.getPaymentMethodId());

            Cartao cartao = new Cartao();
            cartao.setStripePaymentMethodId(atualizado.getId());
            cartao.setBrand(atualizado.getCard().getBrand());
            cartao.setLast4(atualizado.getCard().getLast4());
            cartao.setExpMonth(atualizado.getCard().getExpMonth().intValue());
            cartao.setExpYear(atualizado.getCard().getExpYear().intValue());
            cartao.setUsuario(usuario);

            pagamentoRepository.save(cartao);

            return pagamentoMapper.toResponse(cartao);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar cartão na Stripe", e);
        }
    }

    public List<CartaoResponse> listar() {

        String email = extrairEmailDoToken();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Cartao> pagamentos = pagamentoRepository
                .findByUsuarioId(usuario.getId());

        return pagamentos.stream()
                .map(pagamentoMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deletar(Long id) {

        String email = extrairEmailDoToken();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Cartao cartao = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        // 🔐 Segurança: garante que o cartão pertence ao usuário
        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você não tem permissão para remover este cartão");
        }

        try {

            // 🔥 Configura Stripe
            Stripe.apiKey = stripeSecretKey;

            // 🔥 Recupera o PaymentMethod na Stripe
            PaymentMethod paymentMethod = PaymentMethod.retrieve(cartao.getStripePaymentMethodId());

            // 🔥 Remove do Customer (detach)
            paymentMethod.detach();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover cartão da Stripe", e);
        }

        // 🔥 Remove do banco
        pagamentoRepository.delete(cartao);
    }
}