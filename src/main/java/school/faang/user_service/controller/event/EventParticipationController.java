package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.events.EventRequestDto;
import school.faang.user_service.dto.user.CountResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.events.EventParticipationService;

import java.util.List;


@RestController
@RequestMapping(value = "api/v1/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @GetMapping("/{eventId}")
    public List<UserDto> getAllParticipantsByEventId(@PathVariable
                                                     @NotNull(message = "Event cannot be empty")
                                                     @Positive(message = "Event cannot be negative")
                                                     Long eventId) {
        return eventParticipationService.getAllParticipantsByEventId(eventId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void registerParticipant(@RequestBody @Valid EventRequestDto eventRequestDto) {
        eventParticipationService.registerParticipant(eventRequestDto.eventId(), eventRequestDto.userId());
    }

    @GetMapping("/{eventId}/participants/count")
    public CountResponseDto countParticipantsByEventId(@PathVariable
                                                       @NotNull(message = "Event cannot be empty")
                                                       @Positive(message = "Event cannot be negative")
                                                       Long eventId) {
        return eventParticipationService.countParticipantsByEventId(eventId);
    }

    @PutMapping
    public void unregisterParticipant(@RequestBody @Valid EventRequestDto eventRequestDto) {
        eventParticipationService.unregisteredParticipation(eventRequestDto.eventId(), eventRequestDto.userId());
    }
}
