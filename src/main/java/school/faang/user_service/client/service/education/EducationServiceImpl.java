package school.faang.user_service.client.service.education;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Override
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        validateYear(educationDto);

        Education education = educationMapper.toEntity(educationDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id " +
                        "%d was not found", userId)));
        education.setUser(user);

        return educationMapper.toDto(educationRepository.save(education));
    }

    @Override
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        long educationId = educationDto.getId();

        validateYear(educationDto);

        if (!educationRepository.existsById(educationId)) {
            throw new EntityNotFoundException(String.format("Education by id " +
                    "%d was not found!", educationId));
        }

        Education education = educationMapper.toEntity(educationDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id " +
                        "%d was not found", userId)));
        education.setUser(user);

        return educationMapper.toDto(educationRepository.save(education));
    }

    @Override
    public EducationDto getById(long educationId) {

        return educationRepository.findById
                (educationId).map(educationMapper::toDto).orElseThrow(() -> new
                EntityNotFoundException(String
                .format("Education with id %d was not found",
                        educationId)));
    }

    private void validateYear(EducationDto educationDto) {
        if (educationDto.getYearFrom() >= LocalDate.now().getYear()) {
            throw new DataValidationException("Year of start must be early");
        }
    }
}
