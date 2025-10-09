package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event/{eventId}/participations")
public class EventParticipantController {
    final EventParticipationServiceImpl eventParticipationServiceImpl;

    @PostMapping("/register/{userId}")
    void registerParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationServiceImpl.registerParticipant(eventId, userId);
    }

    @PostMapping("/unregister/{userId}")
    void unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationServiceImpl.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/count")
    CountResponse countParticipantsByEventId(@PathVariable long eventId) {
        return eventParticipationServiceImpl.countParticipantsByEventId(eventId);
    }

    @GetMapping()
    List<UserDto> getAllParticipantsByEventId(@PathVariable long eventId) {
        return eventParticipationServiceImpl.getAllParticipantsByEventId(eventId);
    }
}
