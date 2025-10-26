package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UserAvatarUploadDto(
        @NotNull(message = "Image is required")
        MultipartFile file
) {}
