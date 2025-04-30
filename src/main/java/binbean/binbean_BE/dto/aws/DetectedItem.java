package binbean.binbean_BE.dto.aws;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetectedItem {
    private String key;
    // 개수
    private Integer value;
    // 신뢰도
    private Float confidence;
    private List<PositionDto> positions;
}
