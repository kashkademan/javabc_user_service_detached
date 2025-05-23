package school.faang.user_service.controller.career;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.CareerResponseDto;
import school.faang.user_service.dto.career.CareerUpdateDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.mapper.career.CareerMapper;
import school.faang.user_service.service.career.CareerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/career")
public class CareerController {
    private final CareerService careerService;
    private final CareerMapper careerMapper;

    @PostMapping
    public ResponseEntity<CareerResponseDto> addCareer(
            @Valid @RequestBody CareerCreateDto careerCreateDto) {
        Career career = careerMapper.toCareer(careerCreateDto);
        Career saved = careerService.addCareer(career);
        return new ResponseEntity<>(
                careerMapper.toCareerResponseDto(saved),
                HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<CareerResponseDto> updateCareer(
            @Valid @RequestBody CareerUpdateDto careerUpdateDto) {
        Career career = careerMapper.toCareer(careerUpdateDto);
        Career updated = careerService.updateCareer(career);
        return new ResponseEntity<>(
                careerMapper.toCareerResponseDto(updated),
                HttpStatus.OK);
    }

    @GetMapping("/{careerId}")
    public ResponseEntity<CareerResponseDto> getById(
            @PathVariable("careerId") long careerId) {
        Career career = careerService.getById(careerId);
        return new ResponseEntity<>(
                careerMapper.toCareerResponseDto(career),
                HttpStatus.OK);
    }
}