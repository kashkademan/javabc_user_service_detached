package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.validator.career.CareerValidator;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;
    private final UserContext userContext;

    public CareerDto addCareer(CreateCareerDto createCareerDto) {
        CareerValidator.validateCareerDates(createCareerDto);
        long requesterId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(requesterId);
        Career career = careerMapper.toCareer(createCareerDto);
        career.setUser(user);
        career = careerRepository.save(career);
        return careerMapper.toCareerDto(career);
    }

    public CareerDto getById(long careerId) {
        long requesterId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(requesterId);
        Career career = careerRepository.getByIdOrThrow(careerId);
        CareerValidator.validateOwner(career, user.getId());
        return CareerMapper.toCareerDtoWithUser(career);
    }

    public void deleteCareer(long careerId) {
        long requesterId = userContext.getUserId();
        Career career = careerRepository.getByIdOrThrow(careerId);
        User user = userRepository.getByIdOrThrow(requesterId);
        CareerValidator.validateOwner(career, user.getId());
        careerRepository.delete(career);
    }

    public CareerDto updateCareer(long userId, long careerId, UpdateCareerDto updateCareerDto) {
        CareerValidator.validateCareerDates(updateCareerDto);
        Career career = careerRepository.getByIdOrThrow(careerId);
        CareerValidator.validateOwner(career, userId);
        CareerMapper.update(updateCareerDto, career);
        career = careerRepository.save(career);
        return careerMapper.toCareerDto(career);
    }
}