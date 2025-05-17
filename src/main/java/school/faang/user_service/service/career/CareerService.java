package school.faang.user_service.service.career;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validation.career.CareerValidation;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final UserContext userContext;

    @Transactional
    public Career addCareer(Career career) {
        long userId = userContext.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));

        CareerValidation.validateDateFrom(career);
        CareerValidation.validateDateTo(career);
        CareerValidation.validateDateRanges(career);

        career.setUser(user);
        return careerRepository.save(career);
    }

    @Transactional
    public Career updateCareer(Career career) {
        long userId = userContext.getUserId();

        Career existingCareer = careerRepository.findById(career.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись о карьере не найдена."));

        if (!existingCareer.getUser().getId().equals(userId)) {
            throw new SecurityException("Вы не можете редактировать записи о карьере другого пользователя.");
        }

        if (career.getDateFrom() != null) {
            CareerValidation.validateDateFrom(existingCareer);
            existingCareer.setDateFrom(career.getDateFrom());
        }
        if (career.getDateTo() != null) {
            CareerValidation.validateDateTo(existingCareer);
            existingCareer.setDateTo(career.getDateTo());
        }
        if (career.getCompany() != null) {
            existingCareer.setCompany(career.getCompany());
        }
        if (career.getPosition() != null) {
            existingCareer.setPosition(career.getPosition());
        }

        return careerRepository.save(existingCareer);
    }

    @Transactional(readOnly = true)
    public Career getById(long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("Запись о карьере не найдена"));
    }
}