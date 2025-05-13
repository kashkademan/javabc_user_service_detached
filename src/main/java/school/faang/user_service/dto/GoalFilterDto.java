package school.faang.user_service.dto;

import school.faang.user_service.entity.goal.GoalStatus;
import java.time.LocalDateTime;

public record GoalFilterDto(String title, GoalStatus status, LocalDateTime deadline,
                            Long mentorId, LocalDateTime createdAt, LocalDateTime updatedAt) {}
