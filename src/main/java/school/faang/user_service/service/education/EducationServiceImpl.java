package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDate;
import java.time.Year;

/**
 * EducationServiceImpl — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author Пользователь
 * @since 04.07.2025
 */

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;
    private final EducationMapper educationMapper;

    @Override
    public EducationViewDto addEducation(long userId, EducationViewDto educationDto) {
        validateYearFrom(educationDto.getYearFrom());
        User user = userRepository.getByIdOrThrow(userId);
        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);
        Education saved =  educationRepository.save(education);
        return educationMapper.toEducationDto(saved);
    }

    public EducationViewDto updateEducation(long userId, long educationId, EducationViewDto educationDto) {
        validateYearFrom(educationDto.getYearFrom());
        Education findEducation = educationRepository.getByIdOrThrow(educationDto.getId());
        if (!findEducation.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only update your education");
        }
        Education updatedEducation = educationMapper.toEducation(educationDto);
        updatedEducation.setUser(findEducation.getUser());
        updatedEducation.setId(findEducation.getId());
        Education saved = educationRepository.save(updatedEducation);
        return educationMapper.toEducationDto(saved);
    }

    public void validateYearFrom(Integer yearFrom) {
        if (yearFrom == null) {
            throw new DataValidationException("Не может быть null");
        }
        int currentYear = Year.now().getValue();
        if (currentYear < yearFrom) {
            throw new DataValidationException("You can't start learning in the future");
        }

    }
}
