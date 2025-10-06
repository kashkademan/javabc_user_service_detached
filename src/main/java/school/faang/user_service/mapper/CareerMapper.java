package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.entity.user.Career;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CareerMapper {
    @Mapping(source = "from", target = "dateFrom")
    Career toCareer(CreateCareerDto createCareerDto);

    @Mapping(source = "dateFrom", target = "from")
    CareerDto toCareerDto(Career career);

    static CareerDto toCareerDtoWithUser(Career career) {
        return CareerDto.builder()
                .id(career.getId())
                .userId(career.getUser().getId())
                .to(career.getDateTo())
                .from(career.getDateFrom())
                .company(career.getCompany())
                .position(career.getPosition())
                .build();
    }
}