package school.faang.user_service.service.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.education.CreateEducationDto;
import school.faang.user_service.dto.user.education.EducationDto;
import school.faang.user_service.dto.user.education.UpdateEducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.EducationRepository;

import java.time.Year;

@RequiredArgsConstructor
@Service
@Slf4j

public class EducationServiceImpl implements EducationService {

    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;


    @Override
    public EducationDto addEducation(long userId, @Valid CreateEducationDto educationDto) {
        log.info("Adding education for userId={} with payload={}", userId, educationDto);

        validateEducationDto(educationDto);

        User user = userRepository.getByIdOrThrow(userId);
        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);

        Education saved = educationRepository.save(education);
        return educationMapper.toEducationDto(saved);
    }

    @Override
    public EducationDto updateEducation(long userId, long educationId, @Valid UpdateEducationDto educationDto) {
        log.info("Updating education. userId={}, educationId={}, payload={}", userId, educationId, educationDto);

        validateYearFrom(educationDto.yearFrom());

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education not found with id " + educationId));

        if (!education.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to update this education");
        }

        educationMapper.updateEducationFromDto(educationDto, education);

        educationRepository.save(education);
        return educationMapper.toEducationDto(education);
    }

    @Override
    public EducationDto getById(long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> {
                    log.error("Education not found. id={}", educationId);
                    return new EntityNotFoundException("Education not found. id={}");
                });
        log.info("Education found. id={}, userId={}",
                education.getId(),
                education.getUser() != null ? education.getUser().getId() : null);

        return educationMapper.toEducationDto(education);

    }

    public void validateEducationDto(CreateEducationDto dto) {
        int currentYear = Year.now().getValue();
        if (dto.yearFrom() > currentYear) {
            throw new DataValidationException("YearFrom cannot be greater than the current year");
        }
        if (dto.yearTo() < dto.yearFrom()) {
            throw new DataValidationException("YearTo cannot be earlier than YearFrom");
        }
    }

    private void validateYearFrom(Integer yearFrom) {
        int currentYear = Year.now().getValue();
        if (yearFrom != null && yearFrom > currentYear) {
            throw new DataValidationException("YearFrom cannot be greater than the current year");
        }
    }
}


