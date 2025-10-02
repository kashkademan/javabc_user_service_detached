package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventRequestDto;
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
    public @ResponseBody List<UserDto> getAllParticipantsByEventId(@PathVariable Long eventId) {
        return eventParticipationService.getAllParticipantsByEventId(eventId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public void registerParticipant(@RequestBody EventRequestDto eventRequestDto) {
        eventParticipationService.registerParticipant(eventRequestDto.eventId(), eventRequestDto.userId());
    }

    @GetMapping("/count-users/{eventId}")
    public CountResponse countParticipantsByEventId(@PathVariable Long eventId) {
        return eventParticipationService.countParticipantsByEventId(eventId);
    }

    @PutMapping()
    public void unregisterParticipant(@RequestBody @Valid EventRequestDto eventRequestDto) {
        eventParticipationService.unregisteredParticipation(eventRequestDto.eventId(), eventRequestDto.userId());
    }
}
