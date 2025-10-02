package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.EventParticipationService;

import java.util.List;


@RestController
@RequestMapping(value = "api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @GetMapping("/{eventId}")
    public List<UserDto> getAllParticipantsByEventId(@PathVariable long eventId) {
        return eventParticipationService.getAllParticipantsByEventId(eventId);
    }

    @PostMapping(value = "/register-event/{eventId}/{userId}")
    public void registerParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @GetMapping("/count-users/{eventId}")
    public CountResponse countParticipantsByEventId(@PathVariable long eventId) {
        return eventParticipationService.countParticipantsByEventId(eventId);
    }

    @PutMapping("/unregister-event/{eventId}/{userId}")
    public void unregisterParticipant(@PathVariable long eventId,@PathVariable long userId) {
        eventParticipationService.unregisteredParticipation(eventId, userId);
    }
}
