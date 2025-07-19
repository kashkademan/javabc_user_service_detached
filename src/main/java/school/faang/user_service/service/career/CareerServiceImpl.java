package school.faang.user_service.service.career;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerViewDto;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    @Override
    @Transactional
    public CareerViewDto career(long userId, CareerCreateDto careerDto) {
        log.info("add career for {}", userId);
        User user = userRepository.getByIdOrThrow(userId);
        Career addCareer = careerMapper.toEntity(careerDto, user);
        addCareer.setUser(user);
        careerRepository.save(addCareer);
        return careerMapper.toViewDto(addCareer);
    }

    @Override
    @Transactional
    public CareerViewDto updateCareer(long userId, long careerId, UpdateCareerDto careerDto) {
        log.info("update info career for {}", userId);
        Career career = careerRepository.getByIdOrThrow(careerId);
        if (career.getUser().getId() != userId) {
            throw new ForbiddenException("You can only update your own data.");
        }
        careerMapper.update(careerDto, career);
        career = careerRepository.save(career);
        return careerMapper.toViewDto(career);
    }

    @Override
    @Transactional
    public CareerViewDto getById(long careerId) {
        log.info("getting career data for {}", careerId);
        Career career = careerRepository.getByIdOrThrow(careerId);
        return careerMapper.toViewDto(career);
    }
}
