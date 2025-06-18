package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.resource.S3FileDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegisterRequestDto;
import school.faang.user_service.dto.user.UserRegisterResponseDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.user.UserServiceFacade;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserServiceFacade userService;

    @PostMapping("api/v1/users/register")
    public ResponseEntity<UserRegisterResponseDto> registerUser
            (@RequestBody UserRegisterRequestDto userRegisterRequestDto) {
        UserRegisterResponseDto userRegisterResponseDto = userService.registerUser(userRegisterRequestDto);
        return ResponseEntity.ok(userRegisterResponseDto);
    }

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersById(ids);
    }

    @PutMapping("/api/v1/users/avatar")
    public UserProfilePic uploadAvatar(@RequestBody MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    @GetMapping("/api/v1/users/{userId}/avatar")
    public ResponseEntity<Resource> downloadAvatar(@PathVariable long userId) {
        S3FileDto s3Dto = userService.downloadAvatar(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(s3Dto.getContentType()))
                .contentLength(s3Dto.getContentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s3Dto.getFileName() + "\"")
                .body(s3Dto.getResource());
    }

    @GetMapping("/api/v1/users/{userId}/avatar-mini")
    public ResponseEntity<Resource> downloadAvatarMini(@PathVariable long userId) {
        S3FileDto s3Dto = userService.downloadAvatarMini(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(s3Dto.getContentType()))
                .contentLength(s3Dto.getContentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s3Dto.getFileName() + "\"")
                .body(s3Dto.getResource());
    }

    @DeleteMapping("/api/v1/users/avatar")
    public ResponseEntity<Void> deleteAvatar() {
        userService.deleteAvatar();
        return ResponseEntity.noContent().build();
    }
}