package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationResponseDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.mapper.EducationMapper;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    public void checkYearFrom(EducationResponseDto educationResponseDto) {
        if (!(educationResponseDto.yearFrom().compareTo(LocalDate.now().getYear()) > 0)) {
            return;
        }
        try {
            throw new DataValidationException("Your year is greater than the current year.");
        } catch (DataValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public User checkUserIdEmpty(Long userId) {
        return educationRepository.findById(userId).
                orElseThrow(() -> new DataValidationException("User with ID " + userId + " not found")).getUser();
    }

    public void checkUserIdNull(Long userId) {
        if (userId == null) {
            throw new DataValidationException("User ID is null.");
        }
    }

    public EducationResponseDto saveEducation(Long userId, EducationResponseDto educationResponseDto) {
        checkYearFrom(educationResponseDto);
        checkUserIdNull(userId);
        User user = checkUserIdEmpty(userId);

        Education education = educationMapper.toEducation(educationResponseDto);
        education.setUser(user);

        Education resultEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(resultEducation);
    }

    public EducationResponseDto addEducation(Long userId, EducationResponseDto educationResponseDto) {
        return saveEducation(userId, educationResponseDto);
    }

    public EducationResponseDto updateEducation(Long userId, EducationResponseDto educationResponseDto) {
        checkUserIdNull(userId);

        if (userId != educationResponseDto.id()) {
            throw new DataValidationException("User ID in the path does not match the user ID in the request body");
        }

        return saveEducation(userId, educationResponseDto);
    }

    public EducationResponseDto getById(Long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Education with ID " + educationId + " not found"));
        return educationMapper.toEducationDto(education);
    }
}
