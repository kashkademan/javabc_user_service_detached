package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final EventService service;
    @Value("${event-bucket.count}")
    private Integer eventBucketSize;

    @Scheduled(cron = "${event.delete-scheduled.cron}")
    public void clearEvents() {
        List<Long> pastEventsIds = service.getPastEventsIds();
        if (pastEventsIds.isEmpty()) {
            return;
        }

        for (int i = 0; i < pastEventsIds.size(); i += eventBucketSize) {
            List<Long> sublist = pastEventsIds.subList(i,
                    Math.min(i + eventBucketSize, pastEventsIds.size()));
            service.clearEvents(sublist);
        }
        log.info("Были удалены прошедшие события.");
    }
}
