package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.mapper.career.CareerMapper;
import school.faang.user_service.service.career.CareerService;

@Controller
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;
    private final CareerMapper careerMapper;

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        Career career = careerMapper.toCareer(careerDto);
        Career saved = careerService.addCareer(userId, career);
        return careerMapper.toCareerDto(saved);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        Career career = careerMapper.toCareer(careerDto);
        Career saved = careerService.updateCareer(userId, career);
        return careerMapper.toCareerDto(saved);
    }

    public CareerDto getById(long careerId) {
        Career career = careerService.getById(careerId);
        return careerMapper.toCareerDto(career);
    }
}
