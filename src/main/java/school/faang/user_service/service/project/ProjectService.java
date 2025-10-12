package school.faang.user_service.service.project;

import school.faang.user_service.dto.project.CreateProjectDto;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.dto.project.UpdateProjectDto;

import java.util.List;

public interface ProjectService {
    ProjectDto create(long requesterId, CreateProjectDto createDto);

    ProjectDto update(long requesterId, long projectId, UpdateProjectDto updateDto);

    ProjectDto getById(long requesterId, long projectId);

    List<ProjectDto> getAll(long requesterId);
}
