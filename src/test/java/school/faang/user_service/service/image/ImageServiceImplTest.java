package school.faang.user_service.service.image;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.MinioConfig;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.FileSizeLimitExceededException;
import school.faang.user_service.exception.InvalidFileFormatException;
import school.faang.user_service.exception.StorageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageServiceImpl Tests")
class ImageServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioConfig minioConfig;

    @Mock
    private GetObjectResponse getObjectResponse;

    @InjectMocks
    private ImageServiceImpl imageService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final long TEAM_ID = 1L;
    private static final String AVATAR_KEY = "team-1-avatar-test.jpg";

    @BeforeEach
    void setUp() {
        when(minioConfig.getBucketName()).thenReturn(BUCKET_NAME);
    }

    @Test
    @DisplayName("Should successfully upload team avatar")
    void uploadTeamAvatar_Success() throws Exception {
        byte[] imageBytes = createValidImageBytes();
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "test.jpg",
                "image/jpeg",
                imageBytes
        );

        doNothing().when(minioClient).putObject(any(PutObjectArgs.class));

        String result = imageService.uploadTeamAvatar(file, TEAM_ID);

        assertThat(result).isNotNull();
        assertThat(result).startsWith("team-" + TEAM_ID + "-avatar-");
        assertThat(result).endsWith(".jpg");

        ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(captor.capture());
    }

    @Test
    @DisplayName("Should throw FileSizeLimitExceededException when file is too large")
    void uploadTeamAvatar_FileTooLarge() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        byte[] largeFileBytes = new byte[6 * 1024 * 1024];
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "large.jpg",
                "image/jpeg",
                largeFileBytes
        );

        assertThatThrownBy(() -> imageService.uploadTeamAvatar(file, TEAM_ID))
                .isInstanceOf(FileSizeLimitExceededException.class)
                .hasMessageContaining("exceeds maximum allowed size");

        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should throw InvalidFileFormatException for unsupported file type")
    void uploadTeamAvatar_UnsupportedFileType() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "test.txt",
                "text/plain",
                "not an image".getBytes()
        );

        assertThatThrownBy(() -> imageService.uploadTeamAvatar(file, TEAM_ID))
                .isInstanceOf(InvalidFileFormatException.class)
                .hasMessageContaining("Unsupported file type");

        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should throw InvalidFileFormatException for corrupted image")
    void uploadTeamAvatar_CorruptedImage() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "corrupted.jpg",
                "image/jpeg",
                "not a valid image".getBytes()
        );

        assertThatThrownBy(() -> imageService.uploadTeamAvatar(file, TEAM_ID))
                .isInstanceOf(InvalidFileFormatException.class)
                .hasMessageContaining("Invalid or corrupted image file");

        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should throw StorageException when MinIO upload fails")
    void uploadTeamAvatar_MinioUploadFails() throws Exception {
        byte[] imageBytes = createValidImageBytes();
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "test.jpg",
                "image/jpeg",
                imageBytes
        );

        doThrow(new RuntimeException("MinIO connection failed"))
                .when(minioClient).putObject(any(PutObjectArgs.class));

        assertThatThrownBy(() -> imageService.uploadTeamAvatar(file, TEAM_ID))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Failed to upload avatar to storage")
                .hasCause(new RuntimeException("MinIO connection failed"));
    }

    @Test
    @DisplayName("Should successfully delete team avatar")
    void deleteTeamAvatar_Success() throws Exception {
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        imageService.deleteTeamAvatar(AVATAR_KEY);

        ArgumentCaptor<RemoveObjectArgs> captor = ArgumentCaptor.forClass(RemoveObjectArgs.class);
        verify(minioClient).removeObject(captor.capture());
    }

    @Test
    @DisplayName("Should throw StorageException when MinIO delete fails")
    void deleteTeamAvatar_MinioDeleteFails() throws Exception {
        doThrow(new RuntimeException("MinIO delete failed"))
                .when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertThatThrownBy(() -> imageService.deleteTeamAvatar(AVATAR_KEY))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Failed to delete avatar from storage")
                .hasCause(new RuntimeException("MinIO delete failed"));
    }

    @Test
    @DisplayName("Should successfully get team avatar")
    void getTeamAvatar_Success() throws Exception {
        byte[] expectedBytes = "image data".getBytes();
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);
        when(getObjectResponse.readAllBytes()).thenReturn(expectedBytes);

        byte[] result = imageService.getTeamAvatar(AVATAR_KEY);

        assertThat(result).isEqualTo(expectedBytes);

        ArgumentCaptor<GetObjectArgs> captor = ArgumentCaptor.forClass(GetObjectArgs.class);
        verify(minioClient).getObject(captor.capture());
        verify(getObjectResponse).readAllBytes();
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when avatar not found")
    void getTeamAvatar_NotFound() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("Object not found"));

        assertThatThrownBy(() -> imageService.getTeamAvatar(AVATAR_KEY))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Avatar not found");
    }

    private byte[] createValidImageBytes() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                image.setRGB(x, y, 0xFFFFFF);
            }
        }

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }
}