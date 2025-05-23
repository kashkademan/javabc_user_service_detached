package school.faang.user_service.service;

import school.faang.user_service.dto.CareerDto;

public interface CareerService {
    CareerDto addCareer(Long userId, CareerDto careerDto);

    CareerDto updateCareer(Long userId, CareerDto careerDto);

    CareerDto getById(Long careerId);
}
