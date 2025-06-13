package school.faang.user_service.exception.resource;

import jakarta.persistence.EntityNotFoundException;

public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException(long resourceId) {
        super(String.format("Resource with id %d not found", resourceId));
    }
}
