package binbean.binbean_BE.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record DetectedItem(
    String key,
    Integer value,
    Float confidence, // 신뢰도
    List<Position> positions
) {
    public static DetectedItem create(String key, Integer value, Float confidence, List<Position> positions) {
        return DetectedItem.builder()
            .key(key)
            .value(value)
            .confidence(confidence)
            .positions(positions)
            .build();
    }
}
