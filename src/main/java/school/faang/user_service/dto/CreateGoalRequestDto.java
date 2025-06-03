package school.faang.user_service.dto;

import java.util.List;

public record CreateGoalRequestDto(
        Long userId,
        String title,
        String description,
        Long parentId,
        List<Long> skillIds) {}
