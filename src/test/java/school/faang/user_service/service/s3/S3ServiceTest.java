package school.faang.user_service.service.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.resource.S3FileDto;
import school.faang.user_service.exception.common.FileException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class S3ServiceTest {
    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private S3Service s3Service;

    @Mock
    private MultipartFile multipartFile;

    private final static String BUCKET_NAME = "test-bucket";
    private final static String FOLDER = "avatar";
    private final static String FILE_NAME = "image.jpg";
    private final static String CONTENT_TYPE = "image/jpeg";
    private final static long FILE_SIZE = 123L;
    private final static String KEY = String.format("%s/%s%s", FOLDER, "123456789", FILE_NAME);
    private static final String PRESIGNED_URL = "https://example.com/presigned-url";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        java.lang.reflect.Field field = S3Service.class.getDeclaredField("bucketName");
        field.setAccessible(true);
        field.set(s3Service, BUCKET_NAME);

        java.lang.reflect.Field presignedUrlTtlField = S3Service.class.getDeclaredField("presignedUrlTtl");
        presignedUrlTtlField.setAccessible(true);
        presignedUrlTtlField.set(s3Service, 1);

        when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME);
        when(multipartFile.getContentType()).thenReturn(CONTENT_TYPE);
        when(multipartFile.getSize()).thenReturn(FILE_SIZE);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
    }

    @Test
    void testUploadFileWhenSuccess() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        String resultKey = s3Service.uploadFile(FOLDER, multipartFile);
        assertTrue(resultKey.startsWith(FOLDER));
        assertTrue(resultKey.contains(FILE_NAME));
    }

    @Test
    void testUploadFileWhenThrowsFileException() throws Exception {
        when(multipartFile.getInputStream()).thenThrow(RuntimeException.class);

        assertThrows(FileException.class, () -> s3Service.uploadFile(FOLDER, multipartFile));
    }

    @Test
    void testDeleteFileWhenSuccess() {
        DeleteObjectResponse response = DeleteObjectResponse.builder().build();
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(response);

        s3Service.deleteFile(KEY);

        verify(s3Client).deleteObject((Consumer<DeleteObjectRequest.Builder>) any());
    }

    @Test
    void testDeleteFileWhenThrowsException() {
        when(s3Client.deleteObject((Consumer<DeleteObjectRequest.Builder>) any())).thenThrow(RuntimeException.class);

        assertThrows(FileException.class, () -> s3Service.deleteFile(KEY));
    }

    @Test
    void testDownloadFileWhenSuccess() {
        byte[] data = "test-data".getBytes(StandardCharsets.UTF_8);
        InputStream dataStream = new ByteArrayInputStream(data);
        AbortableInputStream abortableInputStream = AbortableInputStream.create(dataStream);
        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> responseInputStream =
                new ResponseInputStream<>(getObjectResponse, abortableInputStream);
        when(s3Client.getObject((Consumer<GetObjectRequest.Builder>) any())).thenReturn(responseInputStream);
        when(s3Client.headObject((Consumer<HeadObjectRequest.Builder>) any()))
                .thenReturn(HeadObjectResponse.builder()
                        .metadata(Map.of("filename", FILE_NAME))
                        .contentType(CONTENT_TYPE)
                        .contentLength(FILE_SIZE)
                        .build()
                );

        S3FileDto result = s3Service.downloadFile(KEY);

        assertNotNull(result);
        assertEquals(FILE_NAME, result.getFileName());
        assertEquals(CONTENT_TYPE, result.getContentType());
        assertEquals(FILE_SIZE, result.getContentLength());
        assertTrue(result.getResource() instanceof Resource);
    }


    @Test
    void testDownloadFileWhenThrowsException() {
        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(RuntimeException.class);

        assertThrows(FileException.class, () -> s3Service.downloadFile(KEY));
    }

    @Test
    void testGeneratePresignedUrlWhenSuccess() throws MalformedURLException {
        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(new URL(PRESIGNED_URL));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(mockPresignedRequest);

        String resultUrl = s3Service.generatePresignedUrl(KEY);

        assertNotNull(resultUrl);
        assertEquals(PRESIGNED_URL, resultUrl);
        verify(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)));
    }

    @Test
    void testGeneratePresignedUrlWhenThrowsException() {
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenThrow(RuntimeException.class);

        FileException exception = assertThrows(FileException.class, () -> s3Service.generatePresignedUrl(KEY));
        assertEquals("Не удалось создать presigned URL", exception.getMessage());
        verify(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)));
    }
}