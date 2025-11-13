package school.faang.user_service.service.event.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.kafka.EventStartEventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventKey;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventKeyRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SendEventService {

    private final EventStartEventPublisher eventStartEventPublisher;
    private final EventRepository eventRepository;
    private final EventKeyRepository eventKeyRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void notifyEventsInMinute(LocalDateTime targetTime, TimeLeft timeLeft) {
        List<Event> events = eventRepository.findEventsBetweenDates(targetTime.minusMinutes(1),
                targetTime.plusMinutes(1));
        events.forEach(event -> {
            preparingDataForSendingToKafka(event, timeLeft);
        });
    }

    public void preparingDataForSendingToKafka(Event event, TimeLeft timeLeft) {

        List<User> attendeesUser =  userRepository.findAttendeesByEventId(event.getId());
        List<UserDto> attendeesUserDto = attendeesUser.stream()
                .map(userMapper::toUserDto)
                .toList();

        EventStartEventDto eventStartEventDto = new EventStartEventDto(event.getId(),
                event.getOwner().getId(),
                event.getOwner().getUsername(),
                attendeesUserDto,
                event.getTitle(),
                timeLeft);

        String key = formingKey(event, timeLeft);

        checkKeyInBd(eventStartEventDto, key);
    }

    public void checkKeyInBd(EventStartEventDto eventStartEventDto, String key) {
        boolean isKey = eventKeyRepository.existsByKey(key);
        if (isKey) {
            log.info("The event {} have already been processed", eventStartEventDto.eventId());
        } else {
            EventKey eventKey = EventKey.builder()
                    .key(key)
                    .build();
            eventKeyRepository.save(eventKey);
            eventStartEventPublisher.publishEvent(eventStartEventDto);
            log.info("The event {} is ready to be sent to Kafka", eventStartEventDto.eventId());
        }
    }

    private String formingKey(Event event, TimeLeft timeLeft) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getId())
                .append("__")
                .append(timeLeft);
        return sb.toString();
    }
}
