package school.faang.user_service.service.image;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageResizer {

    public MultipartFile resizeMultipartImage(MultipartFile originalFile, int maxSideSize) {
        try {
            BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());


            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            double scale = 1.0;
            if (width > height && width > maxSideSize) {
                scale = (double) maxSideSize / width;
            } else if (height >= width && height > maxSideSize) {
                scale = (double) maxSideSize / height;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String formatName = getFileExtension(originalFile.getOriginalFilename());

            Thumbnails.of(originalImage)
                    .scale(scale)
                    .outputFormat(formatName)
                    .toOutputStream(outputStream);

            return new InMemoryMultipartFile(
                    originalFile.getName(),
                    originalFile.getOriginalFilename(),
                    originalFile.getContentType(),
                    outputStream.toByteArray()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String getFileExtension(String fileName) {
        if (fileName == null) return "jpg";
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex >= 0) ? fileName.substring(dotIndex + 1).toLowerCase() : "jpg";
    }
}
