package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.career.CareerResponse;
import school.faang.user_service.dto.career.CreateCareerRequest;
import school.faang.user_service.dto.career.UpdateCareerRequest;
import school.faang.user_service.entity.user.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {

    Career toEntity(CreateCareerRequest request);

    Career toEntity(UpdateCareerRequest request);

    CareerResponse toResponse(Career career);
}
