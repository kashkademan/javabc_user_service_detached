package school.faang.user_service.service.education;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.EducationCreateDto;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.dto.education.EducationUpdateDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;

import static school.faang.user_service.service.education.Validators.validateUserIsEducationOwner;
import static school.faang.user_service.service.education.Validators.validateYearFromYearTo;

@Slf4j
@RequiredArgsConstructor
@Service
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private final UserContext userContext;

    public EducationDto addEducation(EducationCreateDto educationCreateDto) {
        long userId = userContext.getUserId();

        User user = userRepository.getByIdOrThrow(userId);
        log.info("Добавление образования для пользователя {} с ID: {}",
                user.getUsername(), userId);

        validateYearFromYearTo(educationCreateDto.yearFrom(), educationCreateDto.yearTo());

        Education education = educationMapper.toEducation(educationCreateDto);
        education.setUser(user);
        Education saveEducation = educationRepository.save(education);
        log.info("Образование успешно добавлено для пользователя {} с ID: {}",
                user.getUsername(), saveEducation.getId());

        return educationMapper.toEducationDto(saveEducation);
    }

    public EducationDto updateEducation(long educationId, EducationUpdateDto educationUpdateDto) {
        long userId = userContext.getUserId();

        Education existingEducation = educationRepository.getByIdOrThrow(educationId);
        log.info("Обновление образования с ID: {} для пользователя {}", educationId,
                existingEducation.getUser().getUsername());

        validateYearFromYearTo(educationUpdateDto.yearFrom(), educationUpdateDto.yearTo());
        validateUserIsEducationOwner(userId, existingEducation);

        educationMapper.updateEducationFromDto(educationUpdateDto, existingEducation);
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

        validateUserIsEducationOwner(userId, existingEducation);

        educationRepository.deleteById(educationId);
        log.info("Образование Пользователя {} с ID: {} было удалено", existingEducation.getUser().getUsername(),
                educationId);
        return educationMapper.toEducationDto(existingEducation);
    }
}
