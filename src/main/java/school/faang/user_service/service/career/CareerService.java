package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    public CareerDto addCareer(Long userId, CareerDto careerDto) {
        validateDate(careerDto.getDateFrom(), "from");
        validateDate(careerDto.getDateTo(), "to");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with id %d not found".formatted(userId)));

        Career career = careerMapper.toCareerEntity(careerDto);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    public CareerDto updateCareer(Long userId, CareerDto careerDto) {
        validateDate(careerDto.getDateFrom(), "from");
        validateDate(careerDto.getDateTo(), "to");

        Career existingCareer = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new DataValidationException(
                        String.format("Career not found for id: %d", careerDto.getId())
                ));

        if (!existingCareer.getUser().getId().equals(userId)) {
            throw new DataValidationException(
                    String.format("User id mismatch. Expected: %d, Actual: %d (Career id: %d)",
                            userId,
                            existingCareer.getUser().getId(),
                            careerDto.getId())
            );
        }

        Career updateCareer = careerMapper.toCareerEntity(careerDto);
        updateCareer.setUser(existingCareer.getUser());
        Career savedCareer = careerRepository.save(updateCareer);
        return careerMapper.toCareerDto(savedCareer);
    }

    public CareerDto getById(Long careerId) {
        return careerRepository.findById(careerId)
                .map(careerMapper::toCareerDto)
                .orElseThrow(() -> new DataValidationException("Career with id %d not found".formatted(careerId)));
    }

    private void validateDate(LocalDate date, String fieldName) {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new DataValidationException(
                    String.format("Field '%s' cannot be in the future. Provided: %s", fieldName, date)
            );
        }
    }
}
