package school.faang.user_service.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.entity.resource.ResourceStatus;
import school.faang.user_service.entity.resource.ResourceType;
import school.faang.user_service.exception.FileException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    private final String bucketName = "test-bucket";
    private final String folder = "test-folder";
    private final String fileKey = "folder/file.txt";

    @Mock
    private AmazonS3 s3Client;
    @Mock
    private MultipartFile multipartFile;
    @Mock
    private S3Object s3Object;
    @Mock
    S3ObjectInputStream inputStream;
    @Mock
    ObjectMetadata metadata;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);
    }

    @Test
    void testUploadFileWithMultipartFileShouldUploadSuccessfully() throws IOException {
        String filename = "test.txt";
        String contentType = "text/plain";
        byte[] fileContent = "test content".getBytes();

        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));
        when(multipartFile.getSize()).thenReturn((long) fileContent.length);
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.getContentType()).thenReturn(contentType);

        Resource result = s3Service.uploadFile(multipartFile, folder);

        assertNotNull(result);
        assertEquals(filename, result.getName());
        assertEquals(ResourceType.TEXT, result.getType());
        assertEquals(ResourceStatus.ACTIVE, result.getStatus());
        verify(s3Client).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void testUploadFileWithByteArrayShouldUploadSuccessfully() {
        String filename = "avatar.svg";
        String contentType = "image/svg+xml";
        byte[] fileData = "<svg>test</svg>".getBytes();

        Resource result = s3Service.uploadFile(fileData, filename, contentType, folder);

        assertNotNull(result);
        assertEquals(filename, result.getName());
        assertEquals(ResourceType.IMAGE, result.getType());
        assertEquals(BigInteger.valueOf(fileData.length), result.getSize());
        verify(s3Client).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void testUploadFileWithMultipartFileWhenIoExceptionShouldThrowFileException() throws IOException {
        String originalFileName = "test.txt";
        when(multipartFile.getInputStream()).thenThrow(new IOException("File read error"));
        when(multipartFile.getOriginalFilename()).thenReturn(originalFileName);

        FileException exception = assertThrows(FileException.class,
                () -> s3Service.uploadFile(multipartFile, folder));

        assertEquals("Failed to read file: " + originalFileName, exception.getMessage());
    }

    @Test
    void testUploadFileWhenS3FailsShouldThrowFileException() {
        byte[] fileData = "test".getBytes();
        String filename = "test.txt";
        String contentType = "text/plain";

        when(s3Client.putObject(Mockito.any(PutObjectRequest.class)))
                .thenThrow(new AmazonServiceException("S3 error"));

        FileException exception = assertThrows(FileException.class,
                () -> s3Service.uploadFile(fileData, filename, contentType, folder));

        assertTrue(exception.getMessage().contains("upload failed"));
    }

    @Test
    void testUploadFileShouldGenerateCorrectKeyFormat() {
        byte[] fileData = "test".getBytes();
        String filename = "document.pdf";
        String contentType = "application/pdf";

        Resource result = s3Service.uploadFile(fileData, filename, contentType, folder);

        assertNotNull(result.getKey());
        assertTrue(result.getKey().startsWith(folder + "/"));
        assertTrue(result.getKey().contains(filename));
        verify(s3Client).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void testUploadFileWithDifferentContentTypesShouldSetCorrectResourceType() {
        byte[] fileData = "test".getBytes();

        Map<String, ResourceType> typeMapping = Map.of(
                "image/png", ResourceType.IMAGE,
                "video/mp4", ResourceType.VIDEO,
                "application/pdf", ResourceType.PDF,
                "application/zip", ResourceType.ZIP,
                "text/plain", ResourceType.TEXT
        );

        typeMapping.forEach((contentType, expectedType) -> {
            Resource result = s3Service.uploadFile(fileData, "test", contentType, folder);

            assertEquals(expectedType, result.getType());
        });

        verify(s3Client, times(typeMapping.size())).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void testDeleteFile() {
        doNothing().when(s3Client).deleteObject(bucketName, fileKey);

        s3Service.deleteFile(fileKey);

        verify(s3Client).deleteObject(bucketName, fileKey);
    }

    @Test
    void testDeleteFileShouldThrowAmazonServiceException() {
        doThrow(new AmazonServiceException("S3 error"))
                .when(s3Client).deleteObject(bucketName, fileKey);

        FileException fileException = assertThrows(FileException.class, () -> s3Service.deleteFile(fileKey));
        assertTrue(fileException.getMessage().contains("storage service error"));
    }

    @Test
    void testDeleteFileShouldThrowSdkClientException() {
        doThrow(new SdkClientException("Client error"))
                .when(s3Client).deleteObject(bucketName, fileKey);

        FileException fileException = assertThrows(FileException.class, () -> s3Service.deleteFile(fileKey));
        assertTrue(fileException.getMessage().contains("connection issue"));
    }

    @Test
    void testGetFile() throws IOException {
        byte[] content = "test content".getBytes();
        InputStream contentStream = new ByteArrayInputStream(content);
        S3ObjectInputStream s3InputStream = new S3ObjectInputStream(contentStream, null);

        when(s3Client.getObject(bucketName, fileKey)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3InputStream);
        when(s3Object.getObjectMetadata()).thenReturn(metadata);
        when(metadata.getContentType()).thenReturn("text/plain");

        MultipartFile result = s3Service.getFile(fileKey);

        assertEquals(fileKey, result.getName());
        assertEquals(fileKey, result.getOriginalFilename());
        verify(s3Client).getObject(bucketName, fileKey);
        verify(s3Object).close();
    }

    @Test
    void testGetFileShouldThrowWhenFileNotFound() {
        when(s3Client.getObject(bucketName, fileKey))
                .thenThrow(new AmazonS3Exception("Not found"));

        FileException fileException = assertThrows(FileException.class, () -> s3Service.getFile(fileKey));
        assertTrue(fileException.getMessage().contains("not found"));
    }
}