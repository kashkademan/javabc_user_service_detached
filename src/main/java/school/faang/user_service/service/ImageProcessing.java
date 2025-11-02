package school.faang.user_service.service;

import org.springframework.stereotype.Service;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

@Service
public class ImageProcessing {
    private static int COORDINATE_X = 0;
    private static int COORDINATE_Y = 0;
    private static final float SCALE_NO_CHANGE = 1f;

    public BufferedImage resizeImage(BufferedImage original, int maxSide) {
        if (original == null) {
            throw new IllegalArgumentException("Original image must not be null");
        }

        int width = original.getWidth();
        int height = original.getHeight();

        float scale = Math.min((float) maxSide / width, (float) maxSide / height);

        if (scale >= SCALE_NO_CHANGE) {
            return original;
        }

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        Image scaled = original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(scaled, COORDINATE_X, COORDINATE_Y, null);
        g2d.dispose();

        return resized;
    }
}
