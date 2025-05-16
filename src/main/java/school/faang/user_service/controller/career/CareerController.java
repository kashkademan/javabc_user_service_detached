package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerServiceImpl;

@Controller
@RequiredArgsConstructor
public class CareerController {
    private final CareerServiceImpl careerService;

    public CareerDto addCareer(Long userId, CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    public CareerDto updateCareer(Long userId, CareerDto careerDto) {
        return careerService.updateCareer(userId, careerDto);
    }

    public CareerDto getById(Long careerId) {
        return careerService.getById(careerId);
    }
}
