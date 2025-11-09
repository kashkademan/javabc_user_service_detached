package school.faang.user_service.service.event.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
    public void checkImminentEvents() {
        LocalDateTime dateNow = LocalDateTime.now();

        sendEventStartNotifications(dateNow);

        sendTenMinutesBeforeNotifications(dateNow);

    }

    @Scheduled(fixedDelay = 300000)
    public void checkDistantEvents() {
        LocalDateTime now = LocalDateTime.now();

        sendOneHourBeforeNotifications(now);
        sendFiveHoursBeforeNotifications(now);
        sendDayBeforeNotifications(now);
    }


    public void sendDayBeforeNotifications(LocalDateTime dateDayBeforeBefore) {
        LocalDateTime targetDate = dateDayBeforeBefore.plusDays(1);
        List<Event> events = eventRepository.findEventsBetweenDates(targetDate, targetDate.plusMinutes(5));
        String baseMessage = "Остался 1 день!";
        events.forEach(event -> eventStartEventPublisher.publishEvent(event, baseMessage));
    }


    public void sendFiveHoursBeforeNotifications(LocalDateTime dateFiveHoursBefore) {
        LocalDateTime targetDate = dateFiveHoursBefore.plusHours(5);
        List<Event> events = eventRepository.findEventsBetweenDates(targetDate, targetDate.plusMinutes(5));
        String baseMessage = "Остался 5 часов!";
        events.forEach(event -> eventStartEventPublisher.publishEvent(event, baseMessage));
    }

    public void sendOneHourBeforeNotifications(LocalDateTime dateOneHourBefore) {
        LocalDateTime targetDate = dateOneHourBefore.plusHours(1);
        List<Event> events = eventRepository.findEventsBetweenDates(targetDate, targetDate.plusMinutes(5));
        String baseMessage = "Остался 1 час! ";
        events.forEach(event -> eventStartEventPublisher.publishEvent(event, baseMessage));
    }

    public void sendTenMinutesBeforeNotifications(LocalDateTime dateTenMinutesBefore) {
        LocalDateTime targetDate = dateTenMinutesBefore.plusMinutes(10);
        List<Event> events = eventRepository.findEventsBetweenDates(targetDate, targetDate.plusMinutes(1));
        String baseMessage = "Осталось 10 минут!";
        events.forEach(event -> eventStartEventPublisher.publishEvent(event, baseMessage));
    }

    public void sendEventStartNotifications(LocalDateTime dateNow) {
        List<Event> events = eventRepository.findEventsBetweenDates(dateNow, dateNow.plusMinutes(1));
        String baseMessage = "Начинается сейчас!";
        events.forEach(event -> eventStartEventPublisher.publishEvent(event, baseMessage));
    }
}
