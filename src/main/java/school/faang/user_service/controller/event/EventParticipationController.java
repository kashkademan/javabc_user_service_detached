package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RegisterParticipantRequestDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> registerParticipant(long eventId, long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
        return ResponseEntity.ok("The user has successfully registered for the event.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> unregisterParticipant(long eventId, long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok("The user has been successfully deregistered from the event.");
    }

    @GetMapping
    public ResponseEntity<List<RegisterParticipantRequestDto>> findAllParticipantsByEven(long eventId) {
        List<RegisterParticipantRequestDto> participants = eventParticipationService.getParticipant(eventId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getParticipantsCount(long eventId) {
        int count = eventParticipationService.getParticipantsCount(eventId);
        return ResponseEntity.ok(count);
    }
}