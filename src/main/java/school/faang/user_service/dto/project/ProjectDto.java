package school.faang.user_service.dto.project;

import school.faang.user_service.entity.project.ProjectStatus;

public record ProjectDto(
        Long id,
        String title,
        String description,
        ProjectStatus status,
        Boolean isPrivate
) {
}
