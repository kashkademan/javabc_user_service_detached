package school.faang.user_service.controller.career;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.service.career.CareerService;

@RestController
@RequestMapping("*/career")
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<CareerDto> addCareer(@RequestBody @Valid CreateCareerDto careerDto) {
        long userId = userContext.getUserId();
        return ResponseEntity.ok(careerService.addCareer(userId, careerDto));
    }

    @PutMapping("/{careerId}")
    public ResponseEntity<CareerDto> updateCareer(@PathVariable long careerId, @RequestBody @Valid UpdateCareerDto careerDto) {
        long userId = userContext.getUserId();
        return ResponseEntity.ok(careerService.updateCareer(userId, careerId, careerDto));
    }

    @GetMapping("/{careerId}")
    public ResponseEntity<CareerDto> getById(@PathVariable long careerId) {
        return ResponseEntity.ok(careerService.getById(careerId));
    }
}
