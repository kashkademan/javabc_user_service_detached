package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.facade.user.UserFacade;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserFacade userFacade;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable long userId) {
        log.info("User controller accepted request get user with id {}", userId);

        UserResponseDto response = userFacade.getUserById(userId);
        log.info("User controller return response get user {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getUsersByIds(@RequestParam List<Long> userIds) {
        log.info("User controller accepted request get users with ids {}", userIds);

        List<UserResponseDto> response = userFacade.getUsersByIds(userIds);
        log.info("User controller return response get users {}", response);
        return ResponseEntity.ok(response);
    }
}
