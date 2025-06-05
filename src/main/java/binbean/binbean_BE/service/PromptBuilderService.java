package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.CurrentSeats;
import binbean.binbean_BE.dto.Position;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PromptBuilderService {
    private final ObjectMapper objectMapper;

    public PromptBuilderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String buildPrompt(String encodedImageBytes, int peopleCount, List<Position> peoplePositions, List<Position> seatPositions)
        throws JsonProcessingException {
        String peoplePositionsJson = objectMapper.writeValueAsString(peoplePositions);
        String seatPositionsJson = objectMapper.writeValueAsString(seatPositions);
        // JSON 반환 예시
        String exampleJson = objectMapper.writeValueAsString(
            CurrentSeats.create(List.of(Position.create(10, 10), Position.create(10, 10))));

        return """
            이미지는 다음과 같습니다. %s
            다음은 이미지에서 검출된 사람들의 위치입니다:
            %s

            이미지에서 검출된 사람들의 수는 %d명입니다.
            아래는 도면 상에 정의된 좌석들의 위치입니다:
            %s

            이제 다음 조건에 따라, 사람의 위치를 해당 좌석에 매핑해 주세요:
            1. 사진 이미지의 크기와 비율을 고려해 주세요.
            2. 사진의 왜곡을 평면화 처리 등을 통해 보정한 뒤(Perspective Transform, 투시 변환), 도면 좌석 위치와 정렬해 주세요.
            3. 사진의 회전 정도도 고려해 주세요.
            4. 사람의 위치가 좌석과 충분히 가까운 경우, 해당 좌석에 앉아 있다고 판단해 주세요.

            5. 결과는 사람이 앉아 있는 좌석만 포함하여 아래 형식의 JSON으로 반환해 주세요 (단일 JSON 객체로 응답해 주세요):
            %s
            """.formatted(encodedImageBytes, peoplePositionsJson, peopleCount, seatPositionsJson, exampleJson);
    }
}

