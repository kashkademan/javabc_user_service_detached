package school.faang.user_service.exception.s3;

public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
}