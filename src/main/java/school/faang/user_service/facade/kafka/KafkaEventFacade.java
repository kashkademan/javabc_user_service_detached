package school.faang.user_service.facade.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.publisher.EventKafkaPublisher;

@Component
@RequiredArgsConstructor
public class KafkaEventFacade {

    private final EventMapper eventMapper;
    private final EventKafkaPublisher eventPublisher;

    @Async("kafkaMessageExecutor")
    public void createEvent(Event event) {
        EventResponseDto eventDto = eventMapper.toEventResponseDto(event);
        eventPublisher.sendMessage(eventDto);
    }
}
