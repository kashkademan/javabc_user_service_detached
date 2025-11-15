package school.faang.user_service.mapper;

import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;

public interface CareerMapper {

    static Career toCareer(CreateCareerDto createCareerDto) {
        return Career.builder()
                .dateFrom(createCareerDto.getFrom())
                .dateTo(createCareerDto.getTo())
                .company(createCareerDto.getCompany())
                .position(createCareerDto.getPosition())
                .build();
    }

    static CareerDto toCareerDto(Career career) {
        return CareerDto.builder()
                .id(career.getId())
                .from(career.getDateFrom())
                .to(career.getDateTo())
                .company(career.getCompany())
                .position(career.getPosition())
                .build();
    }

    static void update(UpdateCareerDto dto, Career career) {
        if (dto == null) {
            return;
        }
        if (dto.getFrom() != null) {
            career.setDateFrom(dto.getFrom());
        }
        if (dto.getTo() != null) {
            career.setDateTo(dto.getTo());
        }
        if (dto.getCompany() != null) {
            career.setCompany(dto.getCompany());
        }
        if (dto.getPosition() != null) {
            career.setPosition(dto.getPosition());
        }
    }
}