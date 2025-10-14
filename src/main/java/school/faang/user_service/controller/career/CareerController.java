package school.faang.user_service.controller.career;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.service.career.CareerService;

@RequiredArgsConstructor
@RequestMapping("/career")
@RestController
public class CareerController {
    private final CareerService careerService;

    @PostMapping
    public CareerDto addCareer(@Valid @RequestBody CreateCareerDto createCareerDto) {
        return careerService.addCareer(createCareerDto);
    }

    @GetMapping("/{careerId}")
    public CareerDto getById(@PathVariable long careerId) {
        return careerService.getById(careerId);
    }

    @DeleteMapping("/{careerId}")
    public void deleteById(@PathVariable long careerId) {
        careerService.deleteCareer(careerId);
    }

    @PatchMapping("/{careerId}")
    public ResponseEntity<CareerDto> updateCareer(@PathVariable long careerId,
                                                  @Valid @RequestBody UpdateCareerDto updateCareerDto) {
        CareerDto updatedCareer = careerService.updateCareer(careerId, updateCareerDto);
        return ResponseEntity.ok(updatedCareer);
    }
}