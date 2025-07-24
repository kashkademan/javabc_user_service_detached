package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.career.CareerViewDto;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CareerMapper {

    @Mapping(target = "user", source = "user")
    Career toEntity(CareerCreateDto careerDto, User user);

    void update(UpdateCareerDto careerDto, @MappingTarget Career career);

    CareerViewDto toViewDto(Career career);
}
