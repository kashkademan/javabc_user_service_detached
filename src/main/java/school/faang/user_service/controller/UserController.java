package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<Long> userIds) {
        return ResponseEntity.ok(userService.getUsersByIds(userIds));
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadUsers(@RequestParam("file") MultipartFile file) {
            return ResponseEntity.ok(userService.uploadUsersFromCsv(file));
    }

    @GetMapping("/page")
    public ResponseEntity<List<UserDto>> getUsersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        return ResponseEntity.ok(userService.getUsersByPage(page, size));
    }
}
