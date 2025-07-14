package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class GetUserController {

    private static final String USER_NOT_FOUND_PATTERN = "User %s not found";
    private static final String NOT_VALID_USER_ID_PATTERN = "User id must be not null and positive, but we have: %s";
    private static final String NOT_VALID_LIST_USER_ID = "List of User id must be not empty";

    private final UserService userService;
    private final UserMapper userMapper;

//    @GetMapping("/{userId}")
//    @ResponseStatus(HttpStatus.OK)
//    UserDto getUser(@PathVariable(value = "userId") long id) {
//        validateUserId(id);
//
//        return userService.getUser(id)
//                .map(userMapper::toDto)
//                .orElseThrow(
//                        () -> new NotFoundException(String.format(USER_NOT_FOUND_PATTERN, id)));
//    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getUsers(@RequestBody List<Long> ids) {
        ids = validateListUserId(ids);

        return userService.getUsers(ids).stream()
                .filter(Objects::nonNull)
                .map(userMapper::toDto)
                .toList();
    }

    private void validateUserId(Long id) {
        if (null == id || id < 0) {
            throw new DataValidationException(String.format(NOT_VALID_USER_ID_PATTERN, id));
        }
    }

    private List<Long> validateListUserId(List<Long> ids) {
        List<Long> checkedIds = ids.stream()
                .filter(id -> null != id && id >= 0).toList();
        if (checkedIds.isEmpty()) {
            throw new DataValidationException(NOT_VALID_LIST_USER_ID);
        }
        return checkedIds;
    }
}
