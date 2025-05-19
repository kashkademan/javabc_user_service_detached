package school.faang.user_service.controller.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserAuthDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.external.S3Service;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final S3Service s3Service;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRegistrationDto dto) {
        User user = userService.registerUser(dto);
        UserResponseDto response = userMapper.toResponseDto(user);

        if (user.getUserProfilePic() != null) {
            String fileId = user.getUserProfilePic().getFileId();
            String presignedUrl = s3Service.getPresingnedUrl(fileId).toString();
            response.setAvatarUrl(presignedUrl);
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/avatar/{fileName}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String fileName) {
        byte[] data = s3Service.getFile(fileName);
        String contentType = s3Service.getContentType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") @NotNull MultipartFile file){
        try{
            userService.processCsv(file);
            return ResponseEntity.ok("Users imported successfully");
        }catch (IOException e){
            return ResponseEntity.badRequest().body("Error parcessing file " + e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
        User user = userService.findById(id).orElseThrow(()-> new EntityNotFoundException("User not found"));
        UserDto dto = userMapper.toDto(user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/users/auth/{username}")
    public ResponseEntity<UserAuthDto> getUserAuth(@PathVariable String username) {
        return ResponseEntity.ok(userService.findAuthByUsername(username));
    }
}
