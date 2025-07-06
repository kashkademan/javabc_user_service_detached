package school.faang.user_service.exception;

public class S3FileIOException extends RuntimeException {
    public S3FileIOException(String message) {
        super(message);
    }
}
