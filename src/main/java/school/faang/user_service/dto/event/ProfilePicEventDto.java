package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfilePicEventDto(
        @NotNull
        Long userId,
        @NotBlank
        String profilePicFileId,
        @NotBlank
        String profilePicSmallFileId
) {
}
