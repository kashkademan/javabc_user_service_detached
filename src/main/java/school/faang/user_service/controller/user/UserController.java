package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PersonCsvMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.user.CreateUserService;
import school.faang.user_service.service.user.DeactivateUserService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private static final String NEGATIVE_ID = "userId is negative";

    private final DeactivateUserService deactivateUserService;
    private final CreateUserService createUserService;
    private final UserService userService;
    private final PersonCsvMapper personCsvMapper;
    private final UserMapper userMapper;

    @PostMapping("/deactivate")
    @ResponseBody
    public void deactivateUser(@RequestParam Long userId) {
        if (idIsValid(userId)) {
            deactivateUserService.deactivateUser(userId);
        } else {
            log.error(NEGATIVE_ID);
            throw new DataValidationException(NEGATIVE_ID);
        }
    }

    @GetMapping("{userId}")
    @ResponseBody
    public UserDto getUser(@PathVariable Long userId) {
        if(idIsValid(userId)) {
            User user = deactivateUserService.getUser(userId);
            return userMapper.toDto(user);
        } else {
            log.error(NEGATIVE_ID);
            throw new DataValidationException(NEGATIVE_ID);
        }
    }

    @GetMapping("/active")
    @ResponseBody
    public Page<UserDto> getActiveUsers(
            @RequestParam(defaultValue = "true") boolean active,
            @RequestParam @Min(0) int page,
            @RequestParam @Min(0) int size
    ) {
        return userService.getActiveUsers(active, page, size)
                .map(userMapper::toDto); //ResponseEntity.ok(result);
    }

    private boolean idIsValid(Long id) {
        return id >= 0;
    }

    @PostMapping("/csv")
    @Operation(
            summary = "Create Users from CSV file",
            description = "It parses CSV file. CSV file has to have column like Person.class/PersonCsvMapper.class")
    public List<UserDto> createUsersFromCsvFile(
            @NotNull(message = "Request has to have a CSV file with Persons")
            @RequestBody MultipartFile file)
            throws IOException {

        return createUserService.createUsers(
                personCsvMapper.toPersons(file))
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
