package school.faang.user_service.controller.career;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.service.career.CareerService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;
    private final UserContext userContext;

    @PostMapping
    public CareerDto addCareer(@RequestBody @Valid CareerDto careerDto) {
        long userId = userContext.getUserId();
        return careerService.addCareer(userId, careerDto);
    }

    @PutMapping
    @RequestBody
    public CareerDto updateCareer(@PathVariable long careerId, @RequestBody @Valid CareerDto careerDto) {
        long userId = userContext.getUserId();
        return careerService.updateCareer(userId, careerId, careerDto);
    }

    @GetMapping
    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}
