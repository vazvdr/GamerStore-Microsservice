package com.gamerstore.email_service.services;

import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;
import com.gamerstore.shared.messaging.dto.ItemDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.*;

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

    public void enviarEmailPedidoConfirmado(PaymentConfirmedEvent event) {

        String html = montarHtml(event);

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
        toUser.put("email", event.getUserEmail());
        toUser.put("name", event.getUserName());
        body.put("to", new Object[]{toUser});

        body.put("subject", "Pedido confirmado 🎮 - GamerStore");
        body.put("htmlContent", html);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(brevoApiUrl, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro ao enviar email");
        }
    }

    // ================= HTML =================
    private String montarHtml(PaymentConfirmedEvent event) {

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        StringBuilder itensHtml = new StringBuilder();

        for (ItemDTO item : event.getItems()) {
            itensHtml.append("""
                <tr>
                    <td>%s</td>
                    <td>%d</td>
                    <td>%s</td>
                </tr>
            """.formatted(
                    item.getName(),
                    item.getQuantity(),
                    nf.format(item.getPrice())
            ));
        }

        return """
            <html>
              <body style="font-family: Arial; background: linear-gradient(to right, #27272a, #000000, #27272a); color:#fff; padding:20px;">
                <div style="max-width:600px;margin:auto;background:#000;padding:20px;border-radius:10px;">
                  
                  <h2 style="color:#22c55e;">Pedido Confirmado 🎮</h2>

                  <p>Fala, %s 👋</p>
                  <p>Seu pedido foi confirmado com sucesso!</p>

                  <h3>Resumo do pedido:</h3>

                  <table width="100%%" style="border-collapse: collapse;">
                    <thead>
                      <tr style="background:#000;">
                        <th align="left">Produto</th>
                        <th align="left">Qtd</th>
                        <th align="left">Preço</th>
                      </tr>
                    </thead>
                    <tbody>
                      %s
                    </tbody>
                  </table>

                  <hr style="margin:20px 0;"/>

                  <p>Subtotal: %s</p>
                  <h3>Total: %s</h3>

                  <p style="margin-top:20px;">Obrigado por comprar na GamerStore 🚀</p>
                </div>
              </body>
            </html>
        """.formatted(
                event.getUserName(),
                itensHtml.toString(),
                nf.format(event.getSubtotal()),
                nf.format(event.getAmount())
        );
    }
}