package school.faang.user_service.controller.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.service.UserPictureService;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserPictureService userPictureService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/{userId}/personal")
    public UserPersonalDto getUserPersonal(@PathVariable Long userId) {
        UserPersonalDto userPersonals = userService.getUserPersonals(userId);
        log.debug("Personal info was provided for userid {}", userId);

        return userPersonals;
    }

    @PatchMapping("/{userId}/refresh")
    public UserPersonalDto refreshUsersAvatar(@PathVariable Long userId) {
        UserPersonalDto personalDto = userService.refreshUserAvatar(userId);
        log.debug("Personal photo was refreshed for userid {}, new avatar is {}",
                userId, personalDto.getPictureSmallFileId());

        return personalDto;
    }


    @PostMapping(value = "/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadAvatar(@PathVariable long userId, @RequestPart("file") @NonNull MultipartFile file) {
        userPictureService.uploadAvatar(userId, file);
    }

    @GetMapping("/{userId}/avatar/big")
    public ResponseEntity<byte[]> getAvatarBig(@PathVariable long userId) {
        byte[] avatar = userPictureService.getAvatar(userId, "big");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        log.debug("Avatar was provided for user id {}", userId);

        return new ResponseEntity<>(avatar, headers, HttpStatus.OK);
    }

    @GetMapping("/{userId}/avatar/small")
    public ResponseEntity<byte[]> getAvatarSmall(@PathVariable long userId) {
        byte[] avatar = userPictureService.getAvatar(userId, "small");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        log.debug("Avatar was provided for user id {}", userId);

        return new ResponseEntity<>(avatar, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/avatar")
    public void deleteAvatar(@PathVariable long userId) {
        userPictureService.deleteAvatar(userId);
    }

    @PostMapping("/upload-csv")
    public List<UserDto> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("File not found");
        }

        return userService.processCsv(file);
    }

    @PostMapping("/{userId}/banned")
    public void banUser(@PathVariable Long userId) {
        userService.banUser(userId);
    }

    @PostMapping("/{userId}/unbanned")
    public void unbanUser(@PathVariable Long userId) {
        userService.unbanUser(userId);
    }
}
