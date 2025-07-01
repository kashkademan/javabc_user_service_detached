package school.faang.user_service.exception.s3;

public class FileException extends RuntimeException {
    public FileException(String message) {
        super(message);
    }
}