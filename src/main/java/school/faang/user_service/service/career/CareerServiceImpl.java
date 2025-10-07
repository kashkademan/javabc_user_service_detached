package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    @Override
    public CareerDto addCareer(Long userId, CareerDto careerDto) {

        if (careerDto.from().isAfter(LocalDate.now())
                || careerDto.from().isEqual(LocalDate.now())) {
            log.warn("Start date cannot be in the future. Provided date: {}", careerDto.from());
            throw new DataValidationException("Start date cannot be in the future");
        }

        if (careerDto.to() != null && careerDto.to().isBefore(careerDto.from())) {
            log.warn("End date cannot be before start date. Start: {}, End: {}",
                    careerDto.from(), careerDto.to());
            throw new DataValidationException("End date cannot be before start date");
        }

        User user = userRepository.getByIdOrThrow(userId);

        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    @Override
    public CareerDto updateCareer(Long userId, long careerId, CareerDto careerDto) {

        if (careerDto.from().isAfter(LocalDate.now())
                || careerDto.from().isEqual(LocalDate.now())) {
            log.warn("Start date cannot be in the future. Provided date: {}",
                    careerDto.from());
            throw new DataValidationException("Start date cannot be in the future");
        }

        if (careerDto.to() != null && careerDto.to().isBefore(careerDto.from())) {
            log.warn("End date cannot be before start date. Start: {}, End: {}",
                    careerDto.from(), careerDto.to());
            throw new DataValidationException("End date cannot be before start date");
        }

        Career newCareer = careerRepository.getByIdOrThrow(careerId);

        if (!newCareer.getUser().getId().equals(userId)) {
            log.warn("User {} tried to update career {} that belongs to user {}",
                    userId, careerId, newCareer.getUser().getId());
            throw new ForbiddenException("You can only update your own career data");
        }

        Career careerToUpdate = careerMapper.toCareer(careerDto);
        careerToUpdate.setUser(newCareer.getUser());
        careerToUpdate.setId(careerId);
        Career savedCareer = careerRepository.save(careerToUpdate);
        return careerMapper.toCareerDto(savedCareer);
    }

    @Override
    public CareerDto getById(long careerId) {
        Career career = careerRepository.getByIdOrThrow(careerId);
        log.info("Retrieved career with id: {}", careerId);

        return careerMapper.toCareerDto(career);
    }
}
