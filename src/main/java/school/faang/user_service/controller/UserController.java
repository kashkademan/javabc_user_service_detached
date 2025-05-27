package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.properties.PaginationProperties;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PaginationProperties paginationProperties;

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

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsersByPage(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        int pageValue = page != null ? page : paginationProperties.getDefaultPage();
        int sizeValue = size != null ? size : paginationProperties.getDefaultSize();

        Page<UserDto> users = userService.getUsersByPage(pageValue, sizeValue);
        return ResponseEntity.ok(users.getContent());
    }
}
