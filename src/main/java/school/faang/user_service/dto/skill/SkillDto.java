package school.faang.user_service.dto.skill;

import java.time.LocalDateTime;

public record SkillDto(
        Long id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
