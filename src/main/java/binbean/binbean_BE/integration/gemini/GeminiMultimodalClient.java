package binbean.binbean_BE.integration.gemini;

import binbean.binbean_BE.config.properties.OpenAiChatProperties;
import binbean.binbean_BE.dto.response.GeminiProVisionResponse;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GeminiMultimodalClient {
    private final OpenAiChatProperties chatProperties;
    private final WebClient webClient;

    public GeminiMultimodalClient(OpenAiChatProperties chatProperties, WebClient webClient) {
        this.chatProperties = chatProperties;
        this.webClient = webClient;
    }

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

        /** 2) Gemini API 호출을 위한 WebClient 요청
         * - API 엔드포인트 : completionPath와 API KEY 기반 구성
         * - 요청 본문(requestBody)을 JSON 형식 직렬화하여 POST 요청
         * - 응답 : GeminiProVisionResponse 객체로 역직렬화
         * - block() 호출 통하여 Mono를 동기 방식으로 처리
         */
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
        return response.candidates().getFirst().content.parts.getFirst().text;
    }
}
