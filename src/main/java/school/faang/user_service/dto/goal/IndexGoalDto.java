package school.faang.user_service.dto.goal;

public record IndexGoalDto(
        Long page,
        Long totalPage,
        FilterGoalDto filters
) {
}
