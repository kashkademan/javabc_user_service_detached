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

    private static final byte[] FILE = "test-failure".getBytes(StandardCharsets.UTF_8);
    private static final String FILE_NAME = "fail.svg";
    private static final MediaType TYPE = new MediaType("image", "svg+xml");
    private static final S3Folder FOLDER = S3Folder.AVATARS;
    private static final String EXPECTED_KEY = "avatars/fail_500.svg";
    private static final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET_NAME);
    }

    @Test
    void testUploadFile_uploadAndReturnFileKey() {
        when(s3KeyGenerator.generateKey(FILE_NAME, FOLDER))
                .thenReturn(EXPECTED_KEY);

        String actualKey = s3Service.uploadFile(FILE, FILE_NAME, TYPE, FOLDER);


        assertEquals(EXPECTED_KEY, actualKey);
        verify(s3KeyGenerator).generateKey(FILE_NAME, FOLDER);
        verify(amazonS3).putObject(argThat((PutObjectRequest req) ->
                req.getBucketName().equals(BUCKET_NAME) &&
                        req.getKey().equals(EXPECTED_KEY)
        ));
    }

    @Test
    void testUploadFile_amazonFails() {
        when(s3KeyGenerator.generateKey(FILE_NAME, FOLDER))
                .thenReturn(EXPECTED_KEY);
        doThrow(new AmazonClientException("S3 is down"))
                .when(amazonS3)
                .putObject(any(PutObjectRequest.class));

        assertThrows(FileUploadException.class, () ->
                s3Service.uploadFile(FILE, FILE_NAME, TYPE, FOLDER));

        verify(s3KeyGenerator).generateKey(FILE_NAME, FOLDER);
        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }
}