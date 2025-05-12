package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.service.career.CareerService;

@Controller
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        return careerService.updateCareer(userId, careerDto);
    }

    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}
