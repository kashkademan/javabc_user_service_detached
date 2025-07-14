package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.career.CareerViewDto;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring")
public interface CareerMapper {

    Career toEntity(CareerCreateDto careerDto, User user);

    void update(UpdateCareerDto careerDto, @MappingTarget Career career);

    CareerViewDto toViewDto(Career career);
}
