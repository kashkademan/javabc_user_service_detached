package school.faang.user_service.dto.project;

public record CreateProjectDto(
        String title,
        String description,
        Boolean isPrivate
) {
}
