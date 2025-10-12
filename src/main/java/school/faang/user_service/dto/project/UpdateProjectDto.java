package school.faang.user_service.dto.project;

import school.faang.user_service.entity.project.ProjectStatus;

public record UpdateProjectDto(
        String title,
        String description,
        ProjectStatus status,
        Boolean isPrivate
) {
}
