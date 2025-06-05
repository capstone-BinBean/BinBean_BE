package binbean.binbean_BE.util;

import binbean.binbean_BE.constants.Constants.FixedValue;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtils {

    // 이미지 리사이즈 한 뒤 바이트 배열로 반환
    public static byte[] resizeImage(BufferedImage originalImage, int targetWidth) throws IOException {
        int targetHeight = (int) (originalImage.getHeight() * ((double) targetWidth / originalImage.getWidth()));
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, FixedValue.IMAGE_FORMAT, byteArr);
        // resized image bytes
        return byteArr.toByteArray();
    }
}
