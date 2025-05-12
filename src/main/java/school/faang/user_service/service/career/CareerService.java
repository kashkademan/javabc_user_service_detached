package school.faang.user_service.service.career;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mappers.career.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    private void validateDateFrom(CareerDto careerDto) {
        if (!careerDto.getFrom().isBefore(LocalDate.now())) {
            throw new DataValidationException("Дата начала карьеры не может быть больше текущей даты.");
        }
    }

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        validateDateFrom(careerDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));

        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        validateDateFrom(careerDto);

        Career existingCareer = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись о карьере не найдена."));

        if (userId != existingCareer.getUser().getId()) {
            throw new DataValidationException("У вас нет прав изменять карьеру другого пользователя");
        }

        existingCareer.setDateFrom(careerDto.getFrom());
        existingCareer.setDateTo(careerDto.getTo());
        existingCareer.setCompany(careerDto.getCompany());
        existingCareer.setPosition(careerDto.getPosition());

        Career careerUpdated = careerRepository.save(existingCareer);
        return careerMapper.toCareerDto(careerUpdated);
    }

    public CareerDto getById(long careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("Запись о карьере не найдена"));
        return careerMapper.toCareerDto(career);
    }
}