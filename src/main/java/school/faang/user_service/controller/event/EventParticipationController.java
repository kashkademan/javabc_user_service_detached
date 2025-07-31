package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@Validated
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/register/{userId}")
    public void registerParticipant(
            @PathVariable @Min(value = 1, message = "Event ID must be positive") long eventId,
            @PathVariable @Min(value = 1, message = "User ID must be positive") long userId
    ) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/{eventId}/unregister/{userId}")
    public void unregisterParticipant(
            @PathVariable @Min(value = 1, message = "Event ID must be positive") long eventId,
            @PathVariable @Min(value = 1, message = "User ID must be positive") long userId
    ) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(
            @PathVariable @Min(value = 1, message = "Event ID must be positive") long eventId
    ) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/{eventId}/participants/count")
    public int getParticipantsCount(
            @PathVariable @Min(value = 1, message = "Event ID must be positive") long eventId
    ) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
