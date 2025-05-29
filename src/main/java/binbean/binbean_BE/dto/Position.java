package binbean.binbean_BE.dto;

import lombok.Builder;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;

@Builder
public record Position(double x, double y) {

    public static Position create(double x, double y) {
        return Position.builder()
            .x(x)
            .y(y)
            .build();
    }

    // 클라이언트로 받은 파일 이미지의 사이즈를 통해 픽셀 기준 좌표로 변환
    public static Position convertToPixel(BoundingBox box, int imageWidth, int imageHeight) {
        // 모바일 화면 상 도면 너비/높이
        double floorWidth = 370;
        double floorHeight = 504;

        double scaleX = floorWidth / imageWidth;  // 예: 0.1927
        double scaleY = floorHeight / imageHeight; // 예: 0.4666

        double x = (double) (box.left() * imageWidth);
        double y = (double) (box.top() * imageHeight);
        double w = (double) (box.width() * imageWidth);
        double h = (double) (box.height() * imageHeight);

        // 중심점
        double centerX = x + w / 2;
        double centerY = y + h / 2;

        return Position.builder()
            .x(centerX * scaleX)
            .y(centerY * scaleY)
            .build();
//
//
//        return Position.builder()
//            .x(centerX)
//            .y(centerY)
//            .build();
    }
}
