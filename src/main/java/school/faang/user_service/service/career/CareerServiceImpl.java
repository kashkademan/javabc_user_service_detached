package school.faang.user_service.service.career;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.CareerService;
import school.faang.user_service.validator.CareerValidator;

@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;
    private final CareerValidator careerValidator;

    @Override
    public CareerDto addCareer(Long userId, CareerDto careerDto) {
        careerValidator.validateDate(careerDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        Career career = careerMapper.toCareerEntity(careerDto);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    @Override
    public CareerDto updateCareer(Long userId, CareerDto careerDto) {
        careerValidator.validateDate(careerDto);

        Career existingCareer = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
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

    @Override
    public CareerDto getById(Long careerId) {
        return careerRepository.findById(careerId)
                .map(careerMapper::toCareerDto)
                .orElseThrow(() -> new DataValidationException("Career with id %d not found".formatted(careerId)));
    }
}
