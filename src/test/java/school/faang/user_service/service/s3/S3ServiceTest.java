package school.faang.user_service.service.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.exception.file.FileUploadException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private S3KeyGenerator s3KeyGenerator;
    @InjectMocks
    private S3Service s3Service;

    private final byte[] file = "test-failure".getBytes(StandardCharsets.UTF_8);
    private final String fileName = "fail.svg";
    private final MediaType type = new MediaType("image", "svg+xml");
    private final S3Folder folder = S3Folder.AVATARS;
    private final String expectedKey = "avatars/fail_500.svg";
    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);

    }

    @Test
    void uploadFile_uploadAndReturnFileKey() {
        when(s3KeyGenerator.generateKey(fileName, folder))
                .thenReturn(expectedKey);

        String actualKey = s3Service.uploadFile(file, fileName, type, folder);


        assertEquals(expectedKey, actualKey);
        verify(s3KeyGenerator).generateKey(fileName, folder);
        verify(amazonS3).putObject(argThat((PutObjectRequest req) ->
                req.getBucketName().equals(bucketName) &&
                        req.getKey().equals(expectedKey)
        ));
    }

    @Test
    void uploadFile_amazonFails() {
        when(s3KeyGenerator.generateKey(fileName, folder))
                .thenReturn(expectedKey);
        doThrow(new AmazonClientException("S3 is down"))
                .when(amazonS3)
                .putObject(any(PutObjectRequest.class));

        assertThrows(FileUploadException.class, () ->
                s3Service.uploadFile(file, fileName, type, folder));

        verify(s3KeyGenerator).generateKey(fileName, folder);
        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }
}