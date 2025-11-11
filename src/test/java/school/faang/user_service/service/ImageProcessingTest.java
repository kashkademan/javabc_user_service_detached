package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class ImageProcessingTest {

    private final ImageProcessing imageProcessing = new ImageProcessing();

    @Test
    public void resizeImage_whenOriginalIsNull_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> imageProcessing.resizeImage(null, 100));
    }

    @Test
    public void resizeImage_whenImageSmallerThanMaxSide_shouldReturnOriginal() {
        BufferedImage original = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);

        BufferedImage result = imageProcessing.resizeImage(original, 100);

        assertSame(original, result);
    }

    @Test
    public void resizeImage_whenImageLargerThanMaxSide_shouldResizeProportionally() {
        BufferedImage original = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        int maxSide = 100;

        BufferedImage result = imageProcessing.resizeImage(original, maxSide);
        assertEquals(100, result.getWidth());
        assertEquals(50, result.getHeight());
        assertNotSame(original, result);
    }

    @Test
    public void resizeImage_whenSquareImageLargerThanMaxSide_shouldResizeToSquare() {
        BufferedImage original = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        int maxSide = 100;

        BufferedImage result = imageProcessing.resizeImage(original, maxSide);

        assertEquals(100, result.getWidth());
        assertEquals(100, result.getHeight());
    }

    @Test
    public void resizeImage_whenPortraitImageLargerThanMaxSide_shouldResizeProportionally() {
        BufferedImage original = new BufferedImage(80, 160, BufferedImage.TYPE_INT_RGB);
        int maxSide = 80;

        BufferedImage result = imageProcessing.resizeImage(original, maxSide);

        assertEquals(40, result.getWidth());
        assertEquals(80, result.getHeight());
    }

    @Test
    public void resizeImage_whenLandscapeImageLargerThanMaxSide_shouldResizeProportionally() {
        BufferedImage original = new BufferedImage(160, 80, BufferedImage.TYPE_INT_RGB);
        int maxSide = 80;

        BufferedImage result = imageProcessing.resizeImage(original, maxSide);

        assertEquals(80, result.getWidth());
        assertEquals(40, result.getHeight());
    }

    @Test
    public void resizeImage_whenImageExactlyMaxSide_shouldReturnOriginal() {
        BufferedImage original = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        BufferedImage result = imageProcessing.resizeImage(original, 100);

        assertSame(original, result);
    }

    @Test
    public void resizeImage_shouldCreateNewBufferedImageWithCorrectType() {
        BufferedImage original = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);

        BufferedImage result = imageProcessing.resizeImage(original, 100);

        assertEquals(BufferedImage.TYPE_INT_RGB, result.getType());
        assertNotNull(result);
    }
}