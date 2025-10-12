
package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.project.CreateProjectDto;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.dto.project.UpdateProjectDto;
import school.faang.user_service.entity.project.Project;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toProject(CreateProjectDto projectDto);

    void update(UpdateProjectDto updateProjectDto, @MappingTarget Project project);

    ProjectDto toProjectDto(Project project);
}
