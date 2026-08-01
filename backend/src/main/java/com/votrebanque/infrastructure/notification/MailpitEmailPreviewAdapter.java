package com.votrebanque.infrastructure.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.votrebanque.application.port.outbound.EmailPreviewPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class MailpitEmailPreviewAdapter implements EmailPreviewPort {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MailpitEmailPreviewAdapter(@Value("${app.mailpit.api-url:http://localhost:8025}") String mailpitApiUrl) {
        this.restClient = RestClient.builder().baseUrl(mailpitApiUrl).build();
    }

    @Override
    public Optional<EmailPreview> findLatestEmailTo(String recipientEmail) {
        String query = "to:\"" + recipientEmail + "\"";

        String searchResponseBody = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/search")
                .queryParam("query", query)
                .build())
            .retrieve()
            .body(String.class);

        JsonNode searchResult = readJson(searchResponseBody);

        if (searchResult == null || !searchResult.has("messages") || searchResult.get("messages").isEmpty()) {
            return Optional.empty();
        }

        // The first result is the most recent (Mailpit's default sort order).
        String messageId = searchResult.get("messages").get(0).get("ID").asText();

        String messageResponseBody = restClient.get()
            .uri("/api/v1/message/{id}", messageId)
            .retrieve()
            .body(String.class);

        JsonNode message = readJson(messageResponseBody);

        if (message == null) {
            return Optional.empty();
        }

        String subject = message.path("Subject").asText("");
        String text = message.path("Text").asText("");
        String html = message.path("HTML").asText("");

        return Optional.of(new EmailPreview(subject, text, html));
    }

    private JsonNode readJson(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            return null;
        }
    }
}
