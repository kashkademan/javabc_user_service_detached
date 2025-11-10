package school.faang.user_service.dto.user;

import lombok.Builder;

@Builder
public record CreateUserDto(
        String username,
        String email,
        String password,
        Long countryId
) {
}
