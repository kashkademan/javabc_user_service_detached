package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

/**
 * MentorshipController контроллер для связей менторов и подопечных.
 * <p>
 * Предоставляет эндпоинты для создания, получения и удаления связей менторов и подопечных.
 * </p>*
 *
 * @author fomchenkoandrey
 */
@RestController
@RequestMapping("/mentorships")
@RequiredArgsConstructor
@Tag(name = "Наставничество", description = "Управление связями наставничества между пользователями")
public class MentorshipController {

    private final MentorshipService service;

    @PostMapping("/{mentorId}")
    @Operation(summary = "Добавить наставничество", description = "Создаёт связь наставничества между пользователями")
    public ResponseEntity<Void> addMentorship(@PathVariable long mentorId, long menteeId) {
        service.addMentorship(mentorId, menteeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mentees/{userId}")
    @Operation(summary = "Получить подопечных", description = "Возвращает список пользователей, которых обучает указанный пользователь")
    public ResponseEntity<List<UserDto>> getMentees(@PathVariable long userId) {
        return ResponseEntity.ok(service.getMentees(userId));
    }

    @GetMapping("/mentors/{userId}")
    @Operation(summary = "Получить наставников", description = "Возвращает список пользователей, являющихся наставниками указанного пользователя")
    public ResponseEntity<List<UserDto>> getMentors(@PathVariable long userId) {
        return ResponseEntity.ok(service.getMentors(userId));
    }

    @DeleteMapping("/{menteeId}")
    @Operation(summary = "Удалить наставничество", description = "Удаляет связь наставничества между пользователями")
    public ResponseEntity<Void> deleteMentor(@PathVariable long menteeId, long mentorId) {
        service.deleteMentorship(menteeId, mentorId);
        return ResponseEntity.ok().build();
    }
}
