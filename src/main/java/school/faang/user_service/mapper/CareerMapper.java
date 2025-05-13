package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {
    @Mapping(target = "user", ignore = true)
    Career toCareerEntity(CareerDto careerDto);

    CareerDto toCareerDto(Career career);
}
