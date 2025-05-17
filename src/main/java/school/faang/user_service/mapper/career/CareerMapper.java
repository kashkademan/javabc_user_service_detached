package school.faang.user_service.mapper.career;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.CareerResponseDto;
import school.faang.user_service.dto.career.CareerUpdateDto;
import school.faang.user_service.entity.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {
    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    Career toCareer(CareerCreateDto careerCreateDto);

    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    Career toCareer(CareerUpdateDto careerUpdateDto);

    @Mapping(source = "dateFrom", target = "from")
    @Mapping(source = "dateTo", target = "to")
    CareerResponseDto toCareerResponseDto(Career career);
}