package school.faang.user_service.service.avatar;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.ImageProcessingException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImageProcessingService {

    public BufferedImage resizeImage(BufferedImage originalImage, int targetSize) {
        try {
            return Scalr.resize(originalImage,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.AUTOMATIC,
                    targetSize, targetSize);
        } catch (Exception e) {
            log.error("Error when resizing image", e);
            throw new ImageProcessingException("Image Could Not Be Processed");
        }
    }

    public BufferedImage readImage(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.error("Error reading image", e);
            throw new ImageProcessingException("Could not read the image");
        }
    }

    public byte[] convertToByteArray(BufferedImage image, String format) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error converting image", e);
            throw new ImageProcessingException("Image Could Not Be Converted");
        }
    }

    public int getMaxDimension(BufferedImage image) {
        return Math.max(image.getWidth(), image.getHeight());
    }
}
