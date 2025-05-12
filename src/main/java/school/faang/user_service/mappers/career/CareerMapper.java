package school.faang.user_service.mappers.career;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.entity.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {
    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    Career toCareer(CareerDto careerDto);

    @Mapping(source = "dateFrom", target = "from")
    @Mapping(source = "dateTo", target = "to")
    CareerDto toCareerDto(Career career);
}