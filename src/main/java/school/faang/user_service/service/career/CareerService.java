package school.faang.user_service.service.career;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.career.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validation.career.CareerValidation;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;

    public Career addCareer(long userId, Career career) {
        CareerValidation.validateDateFrom(career);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));

        career.setUser(user);
        return careerRepository.save(career);
    }

    public Career updateCareer(long userId, Career career) {
        CareerValidation.validateDateFrom(career);

        Career existingCareer = careerRepository.findById(career.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись о карьере не найдена."));

        if (userId != existingCareer.getUser().getId()) {
            throw new DataValidationException("У вас нет прав изменять карьеру другого пользователя");
        }

        return careerRepository.save(existingCareer);
    }

    public Career getById(long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("Запись о карьере не найдена"));
    }
}