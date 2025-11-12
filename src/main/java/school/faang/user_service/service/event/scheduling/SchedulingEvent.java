package school.faang.user_service.service.event.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.kafka.EventStartEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventKey;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventKeyRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingEvent {

    private final EventStartEventPublisher eventStartEventPublisher;
    private final EventRepository eventRepository;
    private final EventKeyRepository eventKeyRepository;

    @Scheduled(fixedDelay = 60000)

    public void checkImminentEvents() {
        LocalDateTime dateNow = LocalDateTime.now();

        notifyEventsInMinute(dateNow, TimeLeft.START);
        notifyEventsInMinute(dateNow.plusMinutes(10), TimeLeft.MINUTES_10);
        notifyEventsInMinute(dateNow.plusHours(1), TimeLeft.HOUR_1);
        notifyEventsInMinute(dateNow.plusHours(5), TimeLeft.HOURS_5);
        notifyEventsInMinute(dateNow.plusDays(1), TimeLeft.HOURS_24);
    }

    @Transactional
    public void notifyEventsInMinute(LocalDateTime targetTime, TimeLeft timeLeft) {
        List<Event> events = eventRepository.findEventsBetweenDates(targetTime.minusMinutes(1),
                targetTime.plusMinutes(1));
        events.forEach(event -> {
            preparingDataForSendingToKafka(event, timeLeft);
        });
    }

    public void preparingDataForSendingToKafka(Event event, TimeLeft timeLeft) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getId())
                .append(event.getTitle())
                .append(timeLeft.getMinutes());

        List<Long> attendeesIds = eventRepository.findParticipantIdsByEventId(event.getId());

        EventStartEventDto eventStartEventDto = new EventStartEventDto(event.getId(),
                event.getOwner().getId(),
                event.getOwner().getUsername(),
                attendeesIds,
                event.getTitle(),
                timeLeft);

        checkKeyInBd(eventStartEventDto, sb.toString());
    }

    public void checkKeyInBd(EventStartEventDto eventStartEventDto, String key) {
        boolean isKey = eventKeyRepository.existsByKeyForKafka(key);
        if (isKey) {
            log.info("The event {} have already been processed", eventStartEventDto.eventId());
        } else {
            EventKey eventKey = EventKey.builder()
                    .keyForKafka(key)
                    .build();
            eventKeyRepository.save(eventKey);
            eventStartEventPublisher.publishEvent(eventStartEventDto, key);
            log.info("The event {} is ready to be sent to Kafka", eventStartEventDto.eventId());
        }
    }
}
