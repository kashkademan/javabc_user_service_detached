package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.s3.S3Config;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3AvatarServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private S3Config s3Config;

    @InjectMocks
    private S3AvatarService s3AvatarService;

    @Test
    public void downloadImage_shouldReturnBytes() {
        String objectKey = "test-key";
        String bucketName = "test-bucket";
        byte[] expectedBytes = "test image data".getBytes();

        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStream = new S3ObjectInputStream(
                new ByteArrayInputStream(expectedBytes), null
        );

        when(s3Config.getBucketName()).thenReturn(bucketName);
        when(s3Client.getObject(bucketName, objectKey)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);

        byte[] result = s3AvatarService.downloadImage(objectKey);

        assertArrayEquals(expectedBytes, result);
        verify(s3Client).getObject(bucketName, objectKey);
    }

    @Test
    public void downloadImage_whenIoException_shouldThrowRuntimeException() {
        String objectKey = "test-key";
        String bucketName = "test-bucket";

        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);

        when(s3Config.getBucketName()).thenReturn(bucketName);
        when(s3Client.getObject(bucketName, objectKey)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        try {
            when(inputStream.readAllBytes()).thenThrow(new IOException("Test exception"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertThrows(RuntimeException.class, () -> s3AvatarService.downloadImage(objectKey));
    }

    @Test
    public void getContentType_shouldReturnContentType() {
        String objectKey = "test-key";
        String bucketName = "test-bucket";
        String expectedContentType = "image/png";

        ObjectMetadata metadata = mock(ObjectMetadata.class);

        when(s3Config.getBucketName()).thenReturn(bucketName);
        when(s3Client.getObjectMetadata(bucketName, objectKey)).thenReturn(metadata);
        when(metadata.getContentType()).thenReturn(expectedContentType);

        String result = s3AvatarService.getContentType(objectKey);

        assertEquals(expectedContentType, result);
        verify(s3Client).getObjectMetadata(bucketName, objectKey);
    }

    @Test
    public void uploadImage_shouldUploadAndReturnObjectName() {
        String objectName = "test-object";
        String format = "png";
        String bucketName = "test-bucket";
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        when(s3Config.getBucketName()).thenReturn(bucketName);

        String result = s3AvatarService.uploadImage(image, objectName, format);

        assertEquals(objectName, result);
        verify(s3Client).putObject(eq(bucketName), eq(objectName), any(ByteArrayInputStream.class),
                any(ObjectMetadata.class));
    }

    @Test
    public void uploadImage_whenIoException_shouldThrowRuntimeException() {
        String objectName = "test-object";
        String format = "png";
        String bucketName = "test-bucket";

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        when(s3Config.getBucketName()).thenReturn(bucketName);

        doThrow(new RuntimeException("S3 upload failed"))
                .when(s3Client)
                .putObject(eq(bucketName), eq(objectName), any(ByteArrayInputStream.class), any(ObjectMetadata.class));

        assertThrows(RuntimeException.class, () -> s3AvatarService.uploadImage(image, objectName, format));
    }

    @Test
    public void deleteImage_whenObjectKeyNotNull_shouldDeleteObject() {
        String objectKey = "test-key";
        String bucketName = "test-bucket";

        when(s3Config.getBucketName()).thenReturn(bucketName);

        s3AvatarService.deleteImage(objectKey);

        verify(s3Client).deleteObject(bucketName, objectKey);
    }

    @Test
    public void deleteImage_whenObjectKeyIsNull_shouldDoNothing() {
        s3AvatarService.deleteImage(null);

        verify(s3Client, never()).deleteObject(anyString(), anyString());
    }
}