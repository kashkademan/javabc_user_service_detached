package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;

@RestController
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;

    @PostMapping("/users/{userId}/career")
    public CareerDto addCareer(@PathVariable long userId,
                               @RequestBody CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    @PutMapping("/users/{userId}/career")
    public CareerDto updateCareer(@PathVariable long userId,
                                  @RequestBody CareerDto careerDto) {
        return careerService.updateCareer(userId, careerDto);
    }

    @GetMapping("/career/{careerId}")
    public CareerDto getById(@PathVariable long careerId) {
        return careerService.getById(careerId);
    }
}