package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ChunkDeletionServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ChunkDeletionService chunkDeletionService;

    @Test
    void testDeleteChunk() {
        List<Long> testEventIds = List.of(1L, 2L, 3L, 4L, 5L);
        chunkDeletionService.deleteChunk(testEventIds);
        Mockito.verify(eventRepository).deleteAllById(testEventIds);
    }
}
