package com.gamerstore.user_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.url}")
    private String brevoApiUrl;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.email.from}")
    private String fromEmail;

    @Value("${brevo.email.name}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarEmailRecuperacaoSenha(
            String emailDestino,
            String nomeUsuario,
            String linkRecuperacao
    ) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> body = new HashMap<>();

        // FROM
        Map<String, String> sender = new HashMap<>();
        sender.put("email", fromEmail);
        sender.put("name", fromName);
        body.put("sender", sender);

        // TO
        Map<String, String> toUser = new HashMap<>();
        toUser.put("email", emailDestino);
        toUser.put("name", nomeUsuario);
        body.put("to", List.of(toUser));

        // CONTEÚDO
        body.put("subject", "Recuperação de senha - GamerStore");

        body.put(
                "htmlContent",
                """
                <html>
                  <body style="font-family: Arial, sans-serif;">
                    <h2>Olá, %s 👋</h2>
                    <p>Recebemos uma solicitação para redefinir sua senha.</p>
                    <p>Clique no botão abaixo para continuar:</p>
                    <a href="%s"
                       style="display:inline-block;padding:12px 20px;
                              background:#16a34a;color:#fff;
                              text-decoration:none;border-radius:6px;">
                       Redefinir senha
                    </a>
                    <p style="margin-top:20px;">
                      Se você não solicitou isso, ignore este email.
                    </p>
                    <hr/>
                    <small>GamerStore © 2026</small>
                  </body>
                </html>
                """.formatted(nomeUsuario, linkRecuperacao)
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        brevoApiUrl,
                        request,
                        String.class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro ao enviar email de recuperação");
        }
    }
}
