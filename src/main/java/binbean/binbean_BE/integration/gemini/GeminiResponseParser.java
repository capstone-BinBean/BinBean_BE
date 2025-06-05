package binbean.binbean_BE.integration.gemini;

import binbean.binbean_BE.dto.CurrentSeats;
import binbean.binbean_BE.dto.Position;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeminiResponseParser {

    private final ObjectMapper objectMapper;

    public GeminiResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<CurrentSeats> parse(String rawJson) {
        try {
            String cleanedJson = cleanJsonMarkdown(rawJson);

            // 리스폰스가 배열로 올 경우 처리
            if (cleanedJson.startsWith("[")) {
                List<CurrentSeats> list = objectMapper.readValue(cleanedJson,
                    new TypeReference<>() {});
                List<Position> positions = list.stream()
                    .flatMap(cs -> cs.currentPosition().stream())
                    .toList();
                return Optional.ofNullable(CurrentSeats.create(positions));
            } else {
                return Optional.ofNullable(objectMapper.readValue(cleanedJson, CurrentSeats.class));
            }
        } catch (Exception e) {
            log.warn("Failed to parse Gemini JSON response into CurrentSeats: {}", rawJson, e);
            return Optional.empty();
        }
    }

    public String cleanJsonMarkdown(String rawJson) {
        return rawJson
            .replace("```json", "")
            .replace("```", "")
            .trim();
    }
}
