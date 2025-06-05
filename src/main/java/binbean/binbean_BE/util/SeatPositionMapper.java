package binbean.binbean_BE.util;

import binbean.binbean_BE.dto.Position;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SeatPositionMapper {

    // 이미지 상의 사람의 위치 좌표와 도면 좌표 매핑 (감지된 사람을 가장 가까운 좌석에 매핑)
    public Optional<Position> matchSeatPosition(Position person, List<Position> seatPositions) {
        Position nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Position seat : seatPositions) {
            // 픽셀 좌표 간 유클리드 거리 차이
            double dist = Math.sqrt(Math.pow(person.x() - seat.x(), 2) + Math.pow(person.y() - seat.y(), 2));

            if (dist < minDistance) {
                minDistance = dist;
                nearest = seat;
            }
        }
        // 동적 임계값 계산
        double threshold = getDynamicThreshold(seatPositions);

        // 최대 허용 거리 이내에 사람이 있으면 해당 위치 좌석에 앉았다고 판단
        if (minDistance <= threshold) return Optional.of(nearest);
        else return Optional.empty();
    }

    // 도면 상의 좌석 간 평균 거리 계산
    public double calculateAverageSeatDistance(List<Position> seatPositions) {
        double totalDistance = 0.0;
        int count = 0;
        for (int i=0; i < seatPositions.size(); i++) {
            for (int j=i+1; j < seatPositions.size(); j++) {
                double dx = seatPositions.get(i).x() - seatPositions.get(j).x();
                double dy = seatPositions.get(i).y() - seatPositions.get(j).y();
                double dist = Math.sqrt(dx * dx + dy * dy);

                totalDistance += dist;
                count++;
            }
        }
        return count == 0 ? 0.0 : (totalDistance/count);
    }

    // 평균 좌석 거리 기반 임계값 비율 계산 (사람과 좌석 간 허용 거리)
    public double getDynamicThreshold(List<Position> seatPositions) {
        double avgSeatDistance = calculateAverageSeatDistance(seatPositions);
        return avgSeatDistance * 0.4;
    }
}
