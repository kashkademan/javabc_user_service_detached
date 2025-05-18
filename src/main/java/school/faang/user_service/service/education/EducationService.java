package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.mapper.EducationMapper;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    public void checkYearFrom(EducationDto educationDto) throws DataValidationException {
        if (!(educationDto.getYearFrom().compareTo(LocalDate.now().getYear()) > 0)) {
            return;//The task is to check if the yearFrom is less than the current year.
        }
        throw new DataValidationException("Your year is greater than the current year.");
    }    //However, the yearFrom can be equal to the current year.

    public Optional<User> checkUserIdEmpty(Long userId) throws DataValidationException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new DataValidationException("User with ID " + userId + " not found");
        }
        return userOptional;
    }

    public void checkUserIdNull(Long userId) throws DataValidationException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            throw new DataValidationException("User ID is null.");
        }
    }

    public EducationDto saveEducation(Long userId, EducationDto educationDto) throws DataValidationException {
        checkYearFrom(educationDto);
        checkUserIdNull(userId);
        Optional<User> userOptional = checkUserIdEmpty(userId);

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(userOptional.get());

        Education resultEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(resultEducation);
    }

    public EducationDto addEducation(Long userId, EducationDto educationDto) throws DataValidationException {
        checkYearFrom(educationDto);
        checkUserIdNull(userId);

        return saveEducation(userId, educationDto);
    }

    public EducationDto updateEducation(Long userId, EducationDto educationDto) throws DataValidationException {
        checkYearFrom(educationDto);
        checkUserIdNull(userId);

        if (userId != educationDto.getId()) {
            throw new DataValidationException("User ID in the path does not match the user ID in the request body");
        }

        return saveEducation(userId, educationDto);
    }

    public EducationDto getById(Long educationId) throws DataValidationException {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Education with ID " + educationId + " not found"));
        return educationMapper.toEducationDto(education);
    }
}
