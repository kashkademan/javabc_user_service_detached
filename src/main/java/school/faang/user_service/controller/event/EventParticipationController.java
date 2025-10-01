package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.EventParticipationService;


import java.util.List;


@RestController
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public List<UserDto> getAllParticipantsByEventId(@PathVariable long eventId) {
        return eventParticipationService.getAllParticipantsByEventId(eventId);
    }

    public void registerParticipant(@Valid long eventId, @Valid long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public CountResponse countParticipantsByEventId(long eventId) {
        return eventParticipationService.countParticipantsByEventId(eventId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        eventParticipationService.unregisteredParticipation(eventId, userId);
    }


}
