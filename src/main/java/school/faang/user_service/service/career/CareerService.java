package school.faang.user_service.service.career;

import school.faang.user_service.dto.CareerDto;

public interface CareerService {
    CareerDto addCareer(long userId, CareerDto careerDto);

    CareerDto updateCareer(long userId, CareerDto careerDto);

    CareerDto getById(long careerId);
}
