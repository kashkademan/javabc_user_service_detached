package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @GetMapping
    public ResponseEntity<String> registerParticipant(long eventId, long userId) {
        try {
            eventParticipationService.registerParticipant(eventId, userId);
            return ResponseEntity.ok("The user has successfully registered for the event.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while registering.");
        }
    }

    @GetMapping
    public ResponseEntity<String> unregisterParticipant(long eventId, long userId) {
        try {
            eventParticipationService.unregisterParticipant(eventId, userId);
            return ResponseEntity.ok("The user has been successfully deregistered from the event.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deregistering.");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> findAllParticipantsByEventId(long eventId) {
        try {
            List<User> participants = eventParticipationService.getParticipant(eventId);
            return ResponseEntity.ok(participants);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<Integer> getParticipantsCount(long eventId) {
        try {
            int count = eventParticipationService.getParticipantsCount(eventId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}