package school.faang.user_service.service.event.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.kafka.EventStartEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingEvent {

    private final EventStartEventPublisher eventStartEventPublisher;
    private final EventRepository eventRepository;

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
        notifyEventsInMinute(baseMessage, targetDate);
    }


    public void sendFiveHoursBeforeNotifications(LocalDateTime dateFiveHoursBefore) {
        LocalDateTime targetDate = dateFiveHoursBefore.plusHours(5);
        String baseMessage = "Остался 5 часов!";
        notifyEventsInMinute(baseMessage, targetDate);
    }

    public void sendOneHourBeforeNotifications(LocalDateTime dateOneHourBefore) {
        LocalDateTime targetDate = dateOneHourBefore.plusHours(1);
        String baseMessage = "Остался 1 час! ";
        notifyEventsInMinute(baseMessage, targetDate);
    }

    public void sendTenMinutesBeforeNotifications(LocalDateTime dateTenMinutesBefore) {
        LocalDateTime targetDate = dateTenMinutesBefore.plusMinutes(10);
        String baseMessage = "Осталось 10 минут!";
        notifyEventsInMinute(baseMessage, targetDate);
    }

    public void sendEventStartNotifications(LocalDateTime dateNow) {
        String baseMessage = "Начинается сейчас!";
        notifyEventsInMinute(baseMessage, dateNow);
    }

    public void notifyEventsInMinute(String message, LocalDateTime targetTime) {
        List<Event> events = eventRepository.findEventsBetweenDates(targetTime.minusSeconds(1),
                targetTime.plusMinutes(1));
        events.forEach(event -> {
            preparingDataForSendingToKafka(message, event);
        });
    }

    public void preparingDataForSendingToKafka(String message, Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getId())
                .append(" ")
                .append(message);

        List<Long> attendeesIds = eventRepository.findParticipantIdsByEventId(event.getId());

        EventStartEventDto eventStartEventDto = new EventStartEventDto(event.getId(),
                event.getOwner().getId(),
                attendeesIds,
                event.getTitle());

        eventStartEventPublisher.publishEvent(eventStartEventDto, sb.toString());

    }
}
