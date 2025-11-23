package school.faang.user_service.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.validation.resource.ResourceValidator;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceValidatorTest {

    @Mock
    private MultipartFile file;
    @InjectMocks
    private ResourceValidator resourceValidator;

    @Test
    void testValidateFileSizeWhenSizeExceededShouldThrowException() {
        when(file.getSize()).thenReturn(1024 * 1024 * 6L);
        long maxSizeInMb = 5;

        FileException fileException = assertThrows(FileException.class,
                () -> resourceValidator.validateFileSize(file, maxSizeInMb));
        assertTrue(fileException.getMessage().contains("File size exceeded"));
    }

    @Test
    void testValidateFileSizeWhenSizeWithinLimitShouldNotThrow() {
        when(file.getSize()).thenReturn(1024 * 1024 * 4L);
        long maxSizeInMb = 5;

        Assertions.assertDoesNotThrow(() -> resourceValidator.validateFileSize(file, maxSizeInMb));
    }

    @Test
    void testValidateImageDimensionsWhenInvalidImageShouldThrowException() throws IOException {
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        FileException fileException = assertThrows(FileException.class,
                () -> resourceValidator.validateImageDimensions(file, 100, 100));
        assertTrue(fileException.getMessage().contains("Invalid image file"));
    }

    @Test
    void testValidateImageDimensionsWhenImageNeedsResizeShouldResize() throws IOException {
        BufferedImage originalImage = createTestImage(200, 300);
        MultipartFile file = createMockMultipartFile(originalImage, "test.jpg", "image/jpeg");

        MultipartFile result = resourceValidator.validateImageDimensions(file, 150, 150);

        assertNotNull(result);
        assertEquals("test.jpg", result.getOriginalFilename());
        BufferedImage resultImage = ImageIO.read(result.getInputStream());
        assertTrue(resultImage.getWidth() <= 150);
        assertTrue(resultImage.getHeight() <= 150);
    }

    @Test
    void testValidateImageDimensionsWhenImageWithinLimitsShouldReturnOriginal() throws IOException {
        BufferedImage originalImage = createTestImage(100, 100);
        MultipartFile file = createMockMultipartFile(originalImage, "test.png", "image/png");

        MultipartFile result = resourceValidator.validateImageDimensions(file, 150, 150);

        assertNotNull(result);
        assertEquals("test.png", result.getOriginalFilename());
    }

    @Test
    void testValidateImageDimensionsWhenIoExceptionShouldThrowException() throws IOException {
        when(file.getInputStream()).thenThrow(new IOException("Read error"));

        FileException fileException = assertThrows(FileException.class,
                () -> resourceValidator.validateImageDimensions(file, 100, 100));
        assertTrue(fileException.getMessage().contains("Failed to read file dimensions"));
    }

    private BufferedImage createTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return image;
    }

    private MultipartFile createMockMultipartFile(BufferedImage image, String filename, String contentType)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String format = filename.toLowerCase().endsWith(".png") ? "png" :
                filename.toLowerCase().endsWith(".gif") ? "gif" : "jpg";
        ImageIO.write(image, format, baos);

        return new MockMultipartFile(
                "file",
                filename,
                contentType,
                new ByteArrayInputStream(baos.toByteArray())
        );
    }
}