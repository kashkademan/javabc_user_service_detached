package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CareerMapper {

    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    Career toCareer(CareerDto dto);

    @Mapping(source = "dateFrom", target = "from")
    @Mapping(source = "dateTo", target = "to")
    CareerDto toCareerDto(Career career);
}
