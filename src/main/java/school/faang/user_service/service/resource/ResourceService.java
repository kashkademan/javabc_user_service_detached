package school.faang.user_service.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.exception.resource.ResourceNotFoundException;
import school.faang.user_service.repository.resource.ResourceRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public Resource getResourceById(long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> {
                    log.error("Resource with id {} not found", resourceId);
                    return new ResourceNotFoundException(resourceId);
                });
    }

    @Transactional
    public Resource createResource(Resource resource) {
        Resource savedResource = resourceRepository.save(resource);
        log.info("Resource {} has been saved", savedResource);

        return savedResource;
    }
}
