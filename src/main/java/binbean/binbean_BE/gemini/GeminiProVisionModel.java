package binbean.binbean_BE.gemini;

import binbean.binbean_BE.config.properties.OpenAiChatProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
//import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GeminiProVisionModel {
    private final OpenAiChatProperties chatProperties;
    private final WebClient webClient;

    public GeminiProVisionModel(OpenAiChatProperties chatProperties, WebClient webClient) {
        this.chatProperties = chatProperties;
        this.webClient = webClient;
    }

/**
 * Vision + Text prompt를 받아 Gemini VLM API 호출
 */
    public Mono<String> call(String prompt, String base64Image) {
        // 1) 멀티모달 요청 바디 구성
        Map<String, Object> requestBody = Map.of(
          "contents", List.of(
                Map.of("parts", List.of(
                Map.of("text", prompt),
                Map.of("inline_data", Map.of(
                    "mime_type", "image/jpeg",
                    "data", base64Image
                  ))
                ))
            )
        );

        String model = "gemini-pro-vision";
        return webClient.post()
            .uri(uriBuilder -> uriBuilder
            .path("/{model}:generateContent")
            .queryParam("key", chatProperties.getApiKey())
            .build(model)) // 수정 필요
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(response -> response
            .path("candidates").get(0)
            .path("content")
            .path("parts").get(0)
            .path("text").asText()
        );
    }
}
