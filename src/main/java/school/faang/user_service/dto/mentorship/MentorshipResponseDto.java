package school.faang.user_service.dto.mentorship;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record MentorshipResponseDto(
        int id,

        Long requesterId,
        
        Long receiverId,

        String status,

        String description,

        LocalDateTime createdAt
) {
}


