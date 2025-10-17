package school.faang.user_service.dto.skill;

import school.faang.user_service.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record SkillDto(
        Long id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<UserDto> guarantors
) {
}
