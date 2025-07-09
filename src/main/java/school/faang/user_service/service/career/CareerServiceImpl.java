package school.faang.user_service.service.career;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
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
    public CareerDto addCareer(long userId, CreateCareerDto careerDto) {
        log.info("add career for {}", userId);
        User user = userRepository.getByIdOrThrow(userId);
        Career addCareer = careerMapper.toCareer(careerDto);
        addCareer.setUser(user);
        careerRepository.save(addCareer);
        return careerMapper.toCareerDto(addCareer);
    }

    @Override
    @Transactional
    public CareerDto updateCareer(long userId, long careerId, UpdateCareerDto careerDto) {
        log.info("update info career for {}", userId);
        Career career = careerRepository.getByIdOrThrow(careerId);
        if (career.getUser().getId() != userId) {
            throw new ForbiddenException("You can only update your own data.");
        }
        careerMapper.updateCareerFromDto(careerDto, career);
        career = careerRepository.save(career);
        return careerMapper.toCareerDto(career);
    }


    @Override
    @Transactional
    public CareerDto getById(long careerId) {
        log.info("getting career data for {}", careerId);
        Career career = careerRepository.getByIdOrThrow(careerId);
        return careerMapper.toCareerDto(career);
    }


}
