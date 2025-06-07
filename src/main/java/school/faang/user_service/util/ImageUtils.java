package school.faang.user_service.util;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@UtilityClass
public class ImageUtils {
    private static final int FILE_TYPE_START_INDEX = 6;

    public ByteArrayInputStream resizeImageToFitLongestSide(MultipartFile file, int sideLimit) {
        BufferedImage image = convertFileToStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        validateFileType(file);
        try {
            Thumbnails.of(image)
                    .size(sideLimit, sideLimit)
                    .keepAspectRatio(true)
                    .outputFormat(file.getContentType().substring(FILE_TYPE_START_INDEX))
                    .toOutputStream(outputStream);
        } catch (Exception e) {
            log.error("Exception while resizing image was thrown", e);
            throw new FileException("Exception while resizing image was thrown");
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private BufferedImage convertFileToStream(MultipartFile file) {
        BufferedImage originalImage;
        try (InputStream fileInputStream = file.getInputStream()) {
            originalImage = ImageIO.read(fileInputStream);
        } catch (IOException e) {
            log.error("IOException while converting image was thrown", e);
            throw new FileException("IOException while converting image was thrown");
        }
        if (originalImage == null) {
            log.error("The uploaded file is not a valid image or an unsupported format: {}", file.getOriginalFilename());
            throw new FileException("Uploaded file is not a valid image or format is not supported: %s"
                    .formatted(file.getOriginalFilename()));
        }
        return originalImage;
    }

    private void validateFileType(MultipartFile file) {
        if (file.getContentType() != null && !file.getContentType().startsWith("image")) {
            throw new IllegalArgumentException("Incorrect file type %s".formatted(file.getContentType()));
        }
    }
}
