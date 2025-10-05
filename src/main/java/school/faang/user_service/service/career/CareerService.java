package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;
    private final UserContext userContext;

    public CareerDto addCareer(CreateCareerDto createCareerDto) {
        LocalDate from = createCareerDto.from();

        if (LocalDate.now().isBefore(from)) {
            throw new DataValidationException("Your date is invalid - %s".formatted(from));
        }

        long requesterId = userContext.getUserId();
        Optional<User> optionalUser = userRepository.findById(requesterId);

        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found by id - %s".formatted(requesterId));
        }

        User user = optionalUser.get();
        Career career = careerMapper.toCareer(createCareerDto);
        career.setUser(user);
        career = careerRepository.save(career);
        return careerMapper.toCareerDto(career);
    }

    public CareerDto getById(long careerId) {
        return careerMapper.toCareerDto(careerRepository.findById(careerId).orElseThrow(
                () -> new EntityNotFoundException("Career not found by - %s".formatted(careerId))));
    }

    public void deleteCareer(long careerId) throws AccessDeniedException {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("Career not found with id - %d".formatted(careerId)));

        long currentUserId = userContext.getUserId();

        if (!career.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to delete this career");
        }

        careerRepository.delete(career);
    }
}