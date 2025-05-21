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

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final UserContext userContext;

    private <T> void updateField(T newValue, Career existingCareer,
                                 Consumer<Career> validator,
                                 Consumer<T> setter) {
        Optional.ofNullable(newValue).ifPresent(newFieldValue -> {
            Optional.ofNullable(validator)
                    .ifPresent(fieldValidator -> fieldValidator.accept(existingCareer));
            setter.accept(newFieldValue);
        });
    }

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

        updateField(career.getDateFrom(), existingCareer, CareerValidation::validateDateFrom, existingCareer::setDateFrom);
        updateField(career.getDateTo(), existingCareer, CareerValidation::validateDateTo, existingCareer::setDateTo);
        updateField(career.getCompany(), existingCareer, null, existingCareer::setCompany);
        updateField(career.getPosition(), existingCareer, null, existingCareer::setPosition);

        return careerRepository.save(existingCareer);
    }

    @Transactional(readOnly = true)
    public Career getById(long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("Запись о карьере не найдена"));
    }
}