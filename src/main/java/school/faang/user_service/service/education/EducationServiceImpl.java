package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private final UserContext userContext;

    @Override
    public EducationDto addEducation(Long userId, EducationDto educationDto) {

        if (educationDto.yearFrom() >= LocalDate.now().getYear()) {
            throw new DataValidationException("year must not be later than the current year");
        }

        User user = userRepository.getByIdOrThrow(userId);
        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);
        Education savedEducation =  educationRepository.save(education);

        return educationMapper.toEducationDto(savedEducation);

    }

    @Override
    public EducationDto updateEducation(Long userId, Long educationId, EducationDto educationDto) {

        if (educationDto.yearFrom() >= LocalDate.now().getYear()) {
            throw new DataValidationException("year must not be later than the current year");
        }

        Education userEducation = educationRepository.getByIdOrThrow(educationId);

        if (!Objects.equals(userEducation.getUser().getId(), userId)) {
            throw new ForbiddenException("Обновлять можно только свои данные");
        }

        Education education = educationMapper.toEducation(educationDto);
        Education savedEducation =  educationRepository.save(education);

        return educationMapper.toEducationDto(savedEducation);
    }

    @Override
    public EducationDto getById(Long educationId) {

        Education educationEntity =  educationRepository.getByIdOrThrow(educationId);
        return educationMapper.toEducationDto(educationEntity);

    }


}
