package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record GetUsersDto(
        @NotEmpty(message = "Users ids list cant be empty")
        List<Long> ids
) {
}