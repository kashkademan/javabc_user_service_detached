package school.faang.user_service.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.events.EventServiceImpl;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledEventStart {
    private final EventServiceImpl eventService;

    @Scheduled(cron =  "${schedule.events-cron}")
    public void scheduledEventStart() {
        log.info("Start scheduled Event start");
        eventService.prepareEventsToPublish();
        log.info("Complete scheduled Event start");
    }
}