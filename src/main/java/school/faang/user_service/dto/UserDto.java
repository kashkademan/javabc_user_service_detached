package school.faang.user_service.dto;

import org.springframework.lang.Nullable;

public record UserDto(
        Long id,
        String username,
        String email,
        @Nullable
        String fileId
) {
}
