package school.faang.user_service.controller.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.project.CreateProjectDto;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.dto.project.UpdateProjectDto;
import school.faang.user_service.service.project.ProjectService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    public ProjectDto addProject(CreateProjectDto projectDto) {
        return projectService.create(userContext.getUserId(), projectDto);
    }

    public ProjectDto updateProject(long projectId, UpdateProjectDto projectDto) {
        return projectService.update(userContext.getUserId(), projectId, projectDto);
    }

    public ProjectDto getById(long projectId) {
        return projectService.getById(userContext.getUserId(), projectId);
    }

    public List<ProjectDto> getAll() {
        return projectService.getAll(userContext.getUserId());
    }

}
