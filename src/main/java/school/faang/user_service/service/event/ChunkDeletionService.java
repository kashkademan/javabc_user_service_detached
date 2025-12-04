package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkDeletionService {

    private final EventRepository eventRepository;

    @Transactional
    public void deleteChunk(List<Long> idsToDelete) {
        log.debug("Deleting {} events in a transaction...", idsToDelete.size());
        eventRepository.deleteAllById(idsToDelete);
    }
}
