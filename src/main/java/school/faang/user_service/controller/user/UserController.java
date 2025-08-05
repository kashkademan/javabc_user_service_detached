package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции над пользователями")
public class UserController {
    private final UserService userService;
    private final MentorshipService mentorshipService;

    @PostMapping
    @Operation(summary = "Создать пользователя",
            description = "Создаёт нового пользователя на основе переданных данных")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto userDto) {
        var user = userService.create(userDto);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя по ID")
    public ResponseEntity<UserDto> update(
            @PathVariable long userId,
            @Valid @RequestBody UserUpdateDto userDto) {
        var user = userService.update(userId, userDto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получить пользователя", description = "Возвращает данные пользователя по ID")
    public ResponseEntity<UserDto> getById(
            @PathVariable long userId) {
        var user = userService.getById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск пользователей", description = "Ищет пользователей по заданным фильтрам")
    public ResponseEntity<List<UserDto>> getUsers(
            @Valid @ModelAttribute UserFilterDto filter) {
        var users = userService.getUsers(filter);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{menteeId}/mentor")
    public ResponseEntity<UserDto> getMentor(@PathVariable long menteeId) {
        return ResponseEntity.ok(mentorshipService.getMentor(menteeId));
    }
}
