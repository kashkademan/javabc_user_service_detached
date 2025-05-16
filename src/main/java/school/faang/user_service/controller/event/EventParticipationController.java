package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event-participation")
public class EventParticipationController {
    private final EventParticipationService eventService;

    private final UserMapper userMapper;

    @PutMapping("/{eventId}")
    public void registerParticipant(@PathVariable("eventId") long eventId) {
        eventService.eventParticipant(eventId);
    }

    @DeleteMapping("/{eventId}")
    public void unregisterParticipant(@PathVariable("eventId") long eventId) {
        eventService.unregisterParticipant(eventId);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<UserDto>> getParticipant(@PathVariable("eventId") long eventId) {
        List<User> participants = eventService.getParticipant(eventId);
        if (participants == null || participants.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(userMapper.toEventResponses(participants));
    }

    @GetMapping("/count-user/{eventId}")
    public int getParticipantCount(@PathVariable("eventId") long eventId) {
        return eventService.getParticipantsCount(eventId);
    }
}
