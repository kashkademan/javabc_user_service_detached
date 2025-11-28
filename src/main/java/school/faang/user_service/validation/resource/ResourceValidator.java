package school.faang.user_service.validation.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ResourceValidator {

    public static void validateFileSize(MultipartFile file, long maxPermittedSizeInMb) {
        long fileSizeMaxInBytes = DataSize.ofMegabytes(maxPermittedSizeInMb).toBytes();

        if (file.getSize() > fileSizeMaxInBytes) {
            String errorMessage = "File size exceeded. Actual: %d, permitted: %d"
                    .formatted(file.getSize(), fileSizeMaxInBytes);
            log.error(errorMessage);
            throw new FileException(errorMessage);
        }
    }

    public static MultipartFile validateImageDimensions(MultipartFile file, int maxHeight, int maxWidth) {

        BufferedImage image;
        try (InputStream inputStream = file.getInputStream()) {
            boolean needResize = false;
            image = ImageIO.read(inputStream);

            if (image == null) {
                String errorMessage = "Invalid image file";
                log.error(errorMessage);
                throw new FileException(errorMessage);
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width > maxWidth || height > maxHeight) {
                width = Math.min(width, maxWidth);
                height = Math.min(height, maxHeight);
                needResize = true;
            }

            if (needResize) {
                image = resizeImage(image, width, height);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException("Failed to read file dimensions");
        }

        MultipartFile multipartFile;
        try {
            multipartFile = convertToMultipartFile(image, file);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException(e.getMessage());
        }

        return multipartFile;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }

    private static MultipartFile convertToMultipartFile(BufferedImage image, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String format = "jpg";
        if (originalFilename.toLowerCase().endsWith(".png")) {
            format = "png";
        } else if (originalFilename.toLowerCase().endsWith(".gif")) {
            format = "gif";
        }

        ImageIO.write(image, format, baos);
        byte[] bytes = baos.toByteArray();

        return new MockMultipartFile(
                file.getName(),
                originalFilename,
                contentType,
                new ByteArrayInputStream(bytes)
        );
    }
}
