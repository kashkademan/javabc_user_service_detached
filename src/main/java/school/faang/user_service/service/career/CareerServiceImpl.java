package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerResponse;
import school.faang.user_service.dto.career.CreateCareerRequest;
import school.faang.user_service.dto.career.UpdateCareerRequest;
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
    public CareerResponse addCareer(Long userId, CreateCareerRequest request) {

        if (request.from().isAfter(LocalDate.now())) {
            log.warn("Start date cannot be in the future. Provided date: {}", request.from());
            throw new DataValidationException("Start date cannot be in the future");
        }

        if (request.to() != null && request.to().isBefore(request.from())) {
            log.warn("End date cannot be before start date. Start: {}, End: {}",
                    request.from(), request.to());
            throw new DataValidationException("End date cannot be before start date");
        }

        User user = userRepository.getByIdOrThrow(userId);

        Career career = careerMapper.toEntity(request);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toResponse(savedCareer);
    }

    @Override
    public CareerResponse updateCareer(Long userId, long careerId, UpdateCareerRequest request) {

        if (request.from().isAfter(LocalDate.now())) {
            log.warn("Start date cannot be in the future. Provided date: {}",
                    request.from());
            throw new DataValidationException("Start date cannot be in the future");
        }

        if (request.to() != null && request.to().isBefore(request.from())) {
            log.warn("End date cannot be before start date. Start: {}, End: {}",
                    request.from(), request.to());
            throw new DataValidationException("End date cannot be before start date");
        }

        Career newCareer = careerRepository.getByIdOrThrow(careerId);

        if (!newCareer.getUser().getId().equals(userId)) {
            log.warn("User {} tried to update career {} that belongs to user {}",
                    userId, careerId, newCareer.getUser().getId());
            throw new ForbiddenException("You can only update your own career data");
        }

        Career careerToUpdate = careerMapper.toEntity(request);
        careerToUpdate.setUser(newCareer.getUser());
        careerToUpdate.setId(careerId);
        Career savedCareer = careerRepository.save(careerToUpdate);
        return careerMapper.toResponse(savedCareer);
    }

    @Override
    public CareerResponse getById(long careerId) {
        Career career = careerRepository.getByIdOrThrow(careerId);
        log.info("Retrieved career with id: {}", careerId);

        return careerMapper.toResponse(career);
    }
}
