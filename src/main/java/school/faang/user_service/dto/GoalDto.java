package school.faang.user_service.dto;

import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

public record GoalDto(Long id, String description, Long parentId,
         String title, GoalStatus status, List<Long> skillIds) {}
