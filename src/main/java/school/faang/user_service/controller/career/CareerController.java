package school.faang.user_service.controller.career;

import org.springframework.web.bind.annotation.RequestBody;
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
import school.faang.user_service.dto.career.CareerViewDto;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.rating_service.rating_aspect.ActionType;
import school.faang.user_service.rating_service.rating_aspect.RatingAction;
import school.faang.user_service.service.career.CareerService;

@RestController
@RequestMapping("/careers")
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;
    private final UserContext userContext;

    @PostMapping
    @RatingAction(ActionType.ADD_CAREER)
    public ResponseEntity<CareerViewDto> addCareer(@RequestBody @Valid CareerCreateDto careerDto) {
        long userId = userContext.getUserId();
        return ResponseEntity.ok(careerService.career(userId, careerDto));
    }

    @PutMapping("/{careerId}")
    public ResponseEntity<CareerViewDto> updateCareer(@PathVariable long careerId,
                                                      @RequestBody @Valid UpdateCareerDto careerDto) {
        long userId = userContext.getUserId();
        return ResponseEntity.ok(careerService.updateCareer(userId, careerId, careerDto));
    }

    @GetMapping("/{careerId}")
    public ResponseEntity<CareerViewDto> getById(@PathVariable long careerId) {
        return ResponseEntity.ok(careerService.getById(careerId));
    }
}
