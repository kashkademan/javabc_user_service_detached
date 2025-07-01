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
    public CareerDto addCareer(long userId, CareerDto careerDto) {
        log.info("add career for {}", userId);
        if (careerDto.getFrom() == null || careerDto.getFrom().isAfter(LocalDate.now())) {
            throw new DataValidationException("The variable cannot be created in the future");
        }
        User user = userRepository.getByIdOrThrow(userId);
        Career addCareer = careerMapper.toCareer(careerDto);
        addCareer.setUser(user);
        careerRepository.save(addCareer);
        return careerMapper.toCareerDto(addCareer);
    }

    @Override
    public CareerDto updateCareer(long userId, long careerId, CareerDto careerDto) {
        log.info("update info career for {}", userId);
        if (careerDto.getFrom() == null || careerDto.getFrom().isAfter(LocalDate.now())) {
            throw new DataValidationException("The variable cannot be created in the future");
        }
        Career career = careerRepository.getByIdOrThrow(careerId);
        if (career.getUser().getId() != userId) {
            throw new ForbiddenException("You can only update your own data.");
        }
        Career updateCareer = careerMapper.toCareer(careerDto);
        updateCareer.setId(careerId);
        updateCareer.setUser(career.getUser());
        careerRepository.save(updateCareer);
        return careerMapper.toCareerDto(updateCareer);
    }

    @Override
    public CareerDto getById(long careerId) {
        log.info("getting career data for {}", careerId);
        careerRepository.getByIdOrThrow(careerId);
        return careerMapper.toCareerDto(careerRepository.getById(careerId));
    }


}
