package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.FileData;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User API", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @PostMapping("/register-from-file")
    public ResponseEntity<List<UserDto>> registerUserFromFile(@RequestParam("file") MultipartFile file) {
        List<UserDto> registeredUsers = userService.registerUserFromFile(file);
        return ResponseEntity.ok(registeredUsers);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Найти пользователя", description = "Находит пользователя по его идентификатору")
    public UserDto getUser(@Parameter(description = "Идентификатор пользователя")
                           @PathVariable long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    @Operation(summary = "Найти пользователей",
            description = "Находит группу пользователей по списку их идентификаторов")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PutMapping("/avatar")
    @Operation(summary = "Создание аватара", description = "Создает аватар для данного пользователя системы")
    public ResponseEntity<String> createUserAvatar(@Parameter(description = "Файл изображения для аватара")
                                                   @RequestParam MultipartFile file) {
        userService.createUserAvatar(file);
        return ResponseEntity.ok("Avatar created successfully.");
    }

    @GetMapping("/{userId}/avatar")
    @Operation(summary = "Найти аватар", description = "Находит аватар пользователя с данным идентификатором")
    public ResponseEntity<Resource> getUserAvatar(@Parameter(description = "Идентификатор пользователя")
                                                  @PathVariable Long userId) {
        FileData file = userService.getUserAvatar(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(new InputStreamResource(file.content()));
    }

    @DeleteMapping("/avatar")
    @Operation(summary = "Удаление аватара", description = "Удаляет аватар у данного пользователя системы")
    public ResponseEntity<String> removeUserAvatar() {
        userService.removeUserAvatar();
        return ResponseEntity.ok("Avatar removed successfully.");
    }

    @PutMapping("/deactivate/{id}")
    public UserDto deactivateUser(@PathVariable Long id) {
        return userService.deactivateUser(id);
    }

    @PutMapping("/activate/{id}")
    public UserDto activateUser(@PathVariable Long id) {
        return userService.activateUser(id);
    }

    @PostMapping("/{userId}/addSkill/{skillId}")
    public UserDto addSkill(@PathVariable Long userId, @PathVariable Long skillId) {
        return userService.addSkill(userId, skillId);
    }

    @GetMapping("/count")
    public long getUsersCount() {
        return userService.getUsersCount();
    }
}
