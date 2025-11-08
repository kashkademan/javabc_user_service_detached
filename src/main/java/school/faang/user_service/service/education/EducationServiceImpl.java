package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class EducationServiceImpl implements EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Override
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        validateYearFrom(educationDto.yearFrom());

        User user = userRepository.getByIdOrThrow(userId);
        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);
        log.info("Добавлены данные об образовании для пользователя: {}",
                user.getUsername());
        education = educationRepository.save(education);

        return educationMapper.toEducationDto(education);
    }

    @Override
    public EducationDto updateEducation(long userId, long educationId, EducationDto educationDto) {
        validateYearFrom(educationDto.yearFrom());

        Education education = educationRepository.getByIdOrThrow(educationId);

        if (userId != education.getUser().getId()) {
            String errorMessage = "Попытка обновить не свои данные";
            log.error(errorMessage);
            throw new ForbiddenException(errorMessage);
        }

        Education mapedEducation = educationMapper.toEducation(educationDto);
        mapedEducation.setUser(education.getUser());
        log.info("Данные об образовании обновлены для пользователя: {}",
                education.getUser().getUsername());

        return educationMapper.toEducationDto(educationRepository.save(mapedEducation));
    }

    @Override
    public EducationDto getById(long educationId) {
        Education education = educationRepository.getByIdOrThrow(educationId);

        return educationMapper.toEducationDto(education);
    }

    private void validateYearFrom(int yearFrom) {
        if (yearFrom > LocalDate.now().getYear()) {
            String errorMessage = "Неверный год поступления";
            log.warn(errorMessage);
            throw new DataValidationException(errorMessage);
        }
    }
}
