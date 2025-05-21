package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/api/v1/events")
public class EventParticipationController {
    private final EventParticipationService eventService;
    private final UserMapper userMapper;

    @PutMapping("/register/{eventId}")
    public ResponseEntity<UserDto> registerParticipant(@PathVariable("eventId") long eventId) {
        User registerUser = eventService.registerParticipant(eventId);
        return ResponseEntity.ok(userMapper.userToDto(registerUser));
    }

    @DeleteMapping("/unregister/{eventId}")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable("eventId") long eventId) {
        eventService.unregisterParticipant(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all-register/{eventId}")
    public ResponseEntity<List<UserDto>> getParticipant(@PathVariable("eventId") long eventId) {
        List<User> participants = eventService.getParticipant(eventId);
        return ResponseEntity.ok(userMapper.toEventResponses(participants));
    }

    @GetMapping("/count-register/{eventId}")
    public ResponseEntity<Integer> getParticipantCount(@PathVariable("eventId") long eventId) {
        return ResponseEntity.ok(eventService.getParticipantsCount(eventId));
    }
}
