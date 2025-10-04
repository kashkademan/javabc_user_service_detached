package school.faang.user_service.controller.career;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerResponse;
import school.faang.user_service.dto.career.CreateCareerRequest;
import school.faang.user_service.dto.career.UpdateCareerRequest;
import school.faang.user_service.service.career.CareerService;

@Slf4j
@RestController
@RequestMapping("/career")
@RequiredArgsConstructor
@Validated
public class CareerController {
    private final CareerService careerService;
    private final UserContext userContext;

    @PostMapping
    public CareerResponse addCareer(@Valid @RequestBody CreateCareerRequest request) {
        Long userId = userContext.getUserId();
        log.info("User {} is adding career: {} at {}", userId, request.position(),
                request.company());
        return careerService.addCareer(userId, request);
    }

    @PutMapping("/{careerId}")
    public CareerResponse updateCareer(@PathVariable long careerId,
                                       @Valid @RequestBody UpdateCareerRequest request) {
        Long userId = userContext.getUserId();
        log.info("User {} is updating career {}", userId, careerId);
        return careerService.updateCareer(userId, careerId, request);
    }

    @GetMapping("/{careerId}")
    public CareerResponse getById(@PathVariable long careerId) {
        log.info("Retrieved career with id: {}", careerId);
        return careerService.getById(careerId);
    }
}
