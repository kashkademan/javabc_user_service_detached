package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;

public record FollowRequest (
        @Min(value = 1, message = "id must be a positive number")
        long followerId,

        @Min(value = 1, message = "id must be a positive number")
        long followeeId
) {}
