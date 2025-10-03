package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/participants/{userId}")
    public void registerParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    public void unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants/count")
    public CountResponse countParticipantsByEventId(@PathVariable long eventId) {
        return eventParticipationService.countParticipantsByEventId(eventId);
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getAllParticipantsByEventId(@PathVariable long eventId) {
        return eventParticipationService.getAllParticipantsByEventId(eventId);
    }
}
