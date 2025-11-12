package school.faang.user_service.service.event.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.kafka.EventStartEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventKeyForKafka;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventKeyForKafkaRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingEvent {

    private final EventStartEventPublisher eventStartEventPublisher;
    private final EventRepository eventRepository;
    private final EventKeyForKafkaRepository eventKeyForKafkaRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void checkImminentEvents() {
        LocalDateTime dateNow = LocalDateTime.now();
        log.info("поехали");
        sendEventStartNotifications(dateNow);

        sendTenMinutesBeforeNotifications(dateNow);

        sendOneHourBeforeNotifications(dateNow);

        sendFiveHoursBeforeNotifications(dateNow);

        sendDayBeforeNotifications(dateNow);

    }

    public void sendDayBeforeNotifications(LocalDateTime dateDayBeforeBefore) {
        LocalDateTime targetDate = dateDayBeforeBefore.plusDays(1);
        String baseMessage = "Остался 1 день!";
        notifyEventsInMinute(baseMessage, targetDate, TimeLeft.HOURS_24);
    }


    public void sendFiveHoursBeforeNotifications(LocalDateTime dateFiveHoursBefore) {
        LocalDateTime targetDate = dateFiveHoursBefore.plusHours(5);
        String baseMessage = "Остался 5 часов!";
        notifyEventsInMinute(baseMessage, targetDate, TimeLeft.HOURS_5);
    }

    public void sendOneHourBeforeNotifications(LocalDateTime dateOneHourBefore) {
        LocalDateTime targetDate = dateOneHourBefore.plusHours(1);
        String baseMessage = "Остался 1 час! ";
        notifyEventsInMinute(baseMessage, targetDate, TimeLeft.HOUR_1);
    }

    public void sendTenMinutesBeforeNotifications(LocalDateTime dateTenMinutesBefore) {
        LocalDateTime targetDate = dateTenMinutesBefore.plusMinutes(10);
        String baseMessage = "Осталось 10 минут!";
        notifyEventsInMinute(baseMessage, targetDate, TimeLeft.MINUTES_10);
    }

    public void sendEventStartNotifications(LocalDateTime dateNow) {
        String baseMessage = "Начинается сейчас!";
        notifyEventsInMinute(baseMessage, dateNow, TimeLeft.START);
    }

    public void notifyEventsInMinute(String message, LocalDateTime targetTime, TimeLeft timeLeft) {
        List<Event> events = eventRepository.findEventsBetweenDates(targetTime.minusSeconds(1),
                targetTime.plusMinutes(1));
        events.forEach(event -> {
            preparingDataForSendingToKafka(message, event, timeLeft);
        });
    }

    public void preparingDataForSendingToKafka(String message, Event event, TimeLeft timeLeft) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getId())
                .append(" ")
                .append(message);

        List<Long> attendeesIds = eventRepository.findParticipantIdsByEventId(event.getId());

        EventStartEventDto eventStartEventDto = new EventStartEventDto(event.getId(),
                event.getOwner().getId(),
                attendeesIds,
                event.getTitle(),
                timeLeft);

        checkKeyInKafka(eventStartEventDto, sb.toString());

    }

    public void checkKeyInKafka(EventStartEventDto eventStartEventDto, String key) {

        boolean isKey = eventKeyForKafkaRepository.existsByKeyForKafka(key);
        if (isKey) {
            log.info("The event {} have already been processed", eventStartEventDto.eventId());
        } else {
            EventKeyForKafka eventKeyForKafka = EventKeyForKafka.builder()
                    .keyForKafka(key)
                    .build();
            eventKeyForKafkaRepository.save(eventKeyForKafka);
            eventStartEventPublisher.publishEvent(eventStartEventDto, key);
            log.info("The event {} is ready to be sent to Kafka", eventStartEventDto.eventId());
        }
    }
}
