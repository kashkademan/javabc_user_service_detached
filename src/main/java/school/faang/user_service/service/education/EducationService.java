package school.faang.user_service.service.education;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.Year;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private final UserContext userContext;

    public EducationDto addEducation(CreateEducationDto createEducationDto) {
        long userId = userContext.getUserId();
        log.info("Добавление образования для пользователя с ID: {}", userId);

        validateYearFrom(createEducationDto.yearFrom());

        User user = userRepository.getByIdOrThrow(userId);

        Education education = educationMapper.toEducation(createEducationDto);

        education.setUser(user);

        Education saveEducation = educationRepository.save(education);
        log.info("Образование успешно добавлено с ID: {}", saveEducation.getId());

        return educationMapper.toEducationDto(saveEducation);
    }

    public EducationDto updateEducation(long educationId, UpdateEducationDto updateEducationDto) {
        long userId = userContext.getUserId();
        log.info("Обновление образования с ID: {} для пользователя с ID: {}", educationId, userId);

        validateYearFrom(updateEducationDto.yearFrom());

        Education existingEducation = educationRepository.getByIdOrThrow(educationId);
        validateUserIsEducationOwner(userId, existingEducation);

        educationMapper.updateEducationFromDto(updateEducationDto, existingEducation);
        Education updateEducation = educationRepository.save(existingEducation);
        return educationMapper.toEducationDto(updateEducation);
    }

    public EducationDto getById(long educationId) {
        long userId = userContext.getUserId();
        Education existingEducation = educationRepository.getByIdOrThrow(educationId);
        validateUserIsEducationOwner(userId, existingEducation);
        return educationMapper.toEducationDto(existingEducation);
    }

    public EducationDto deleteEducation(long educationId) {
        long userId = userContext.getUserId();
        Education existingEducation = educationRepository.getByIdOrThrow(educationId);
        educationRepository.deleteById(educationId);
        log.info("Данные с ID: {} были удалены", educationId);
        validateUserIsEducationOwner(userId, existingEducation);
        return educationMapper.toEducationDto(existingEducation);
    }

    public void validateYearFrom(Integer yearFrom) {
        if (yearFrom != null && yearFrom > Year.now().getValue()) {
            log.warn("Попытка добавить образование с годом начала в будущем: {}", yearFrom);
            throw new DataValidationException("Год начала обучения не может быть больше текущего");
        }
    }

    public void validateUserIsEducationOwner(long userId, Education education) {
        User educationOwner = education.getUser();

        if (!Objects.equals(userId, educationOwner.getId())) {
            throw new ForbiddenException("Не достаточно прав для получения этих данных");
        }
    }
}
