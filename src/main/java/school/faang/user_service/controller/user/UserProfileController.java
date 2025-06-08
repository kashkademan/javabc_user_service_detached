package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserProfileDto;
import school.faang.user_service.mapper.UserProfileMapper;
import school.faang.user_service.service.user.UserProfileService;

@RestController
@RequestMapping("/api/v1/user-profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService profileService;
    private final UserProfileMapper userProfileMapper;

    @GetMapping("/presigned-url")
    public ResponseEntity<UserProfileDto> generatePresignedUrl(
            @RequestParam long userId,
            @RequestParam boolean isSmall
    ) {
        String presignedUrl = profileService.generatePresignedUrl(userId, isSmall);
        UserProfileDto result = userProfileMapper.toUserProfileDto(presignedUrl);

        return ResponseEntity.ok(result);
    }
}