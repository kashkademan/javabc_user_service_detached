package school.faang.user_service.service.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.exception.resource.ResourceNotFoundException;
import school.faang.user_service.repository.resource.ResourceRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    private ResourceService resourceService;
    private Resource resource;

    @BeforeEach
    public void setUp() {
        resource = new Resource();
        resource.setId(11L);
    }

    @Test
    public void testGetResourceById_successfully() {
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));

        Resource returnResource = resourceService.getResourceById(resource.getId());

        verify(resourceRepository, times(1)).findById(resource.getId());
        assertEquals(resource.getId(), returnResource.getId());
    }

    @Test
    public void testGetResourceById_resourceNotFound() {
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.getResourceById(resource.getId()));
        verify(resourceRepository, times(1)).findById(resource.getId());
    }

    @Test
    void testCreateResource_successfully() {
        when(resourceRepository.save(resource)).thenReturn(resource);

        Resource result = resourceService.createResource(resource);

        assertNotNull(result);
        assertEquals(resource, result);
        verify(resourceRepository).save(resource);
    }
}
