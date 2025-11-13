package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserIdsRequest(
        @NotEmpty(message = "Ids list cannot be empty")
        List<Long> userIds
) {
}
