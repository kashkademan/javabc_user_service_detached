package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
public class ExpiredEventCleanupScheduler {

    private final EventService eventService;

    @Value("${cleanup.expired-event.chunk-size}")
    private int chunkSize;

    @Scheduled(cron = "${cleanup.expired-event.cron}")
    public void clearEvents() {
        eventService.clearPastEvents(chunkSize);
    }
}