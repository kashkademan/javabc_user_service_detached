package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Min;

public record GetMentorsResponseDto(
        @Min(value = 1, message = "id must be a positive number")
        long id,

        String username
){}