package school.faang.user_service.service.education.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.education.EducationService;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final UserContext userContext;

    @Override
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        if (userContext.getUserId() != userId) {
            throw new ForbiddenException("Операция запрещена пользователю с id " + userId);
        }
        if (educationDto.getYearFrom() > LocalDate.now().getYear()) {
            throw new RuntimeException("Год начала обучения не может быть больше текущего");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));

        EducationMapper mapper = EducationMapper.INSTANCE;
        Education education = mapper.toEducation(educationDto);
        education.setUser(user);
        educationRepository.save(education);
        log.debug("Успешно добавили пользователю с id " + userId + " образование");
        return educationDto;
    }

    @Override
    public void updateEducation(long educationId, EducationDto educationDto) {
        if (educationDto.getYearFrom() > LocalDate.now().getYear()) {
            throw new RuntimeException("Год начала обучения не может быть больше текущего");
        }

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Образование с id " + educationId + " не найдено"));
        if (userContext.getUserId() != education.getUser().getId()) {
            throw new ForbiddenException("Запрещено изменять чужое образование");
        }
        education.setYearFrom(educationDto.getYearFrom());
        education.setYearTo(educationDto.getYearTo());
        education.setEducationLevel(educationDto.getEducationLevel());
        education.setInstitution(educationDto.getInstitution());
        education.setSpecialization(educationDto.getSpecialization());
        educationRepository.save(education);
    }

    @Override
    public EducationDto getEducationById(long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Образование с id " + educationId + " не найдено"));

        EducationMapper mapper = EducationMapper.INSTANCE;
        return mapper.toEducationDto(education);
    }


}
