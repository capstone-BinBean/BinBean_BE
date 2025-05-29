package binbean.binbean_BE.gemini;

import binbean.binbean_BE.config.properties.OpenAiChatProperties;
import binbean.binbean_BE.dto.response.GeminiProVisionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
//import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.http.MediaType;
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
    public String call(String prompt, String base64Image) {
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

        GeminiProVisionResponse response = webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path(chatProperties.getCompletionsPath())
                .queryParam("key", chatProperties.getApiKey())
                .build())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(GeminiProVisionResponse.class)
            .block();

        // 3) 응답 유효성 검사
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new IllegalStateException("Empty response from Gemini VLM API");
        }

        // 4) 첫 번째 후보 텍스트 추출
        return response.candidates().get(0).content.parts.get(0).text;
    }
}
