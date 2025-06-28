package school.faang.user_service.service.education;

<<<<<<<< HEAD:src/main/java/school/faang/user_service/client/service/education/EducationService.java
========
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
>>>>>>>> kelpie-master-stream10:src/main/java/school/faang/user_service/service/education/EducationServiceImpl.java
import school.faang.user_service.dto.EducationDto;

<<<<<<<< HEAD:src/main/java/school/faang/user_service/client/service/education/EducationService.java
public interface EducationService {

    EducationDto addEducation(long userId, EducationDto educationDto);

    EducationDto updateEducation(long userId, EducationDto educationDto);

    EducationDto getById(long educationId);

========
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
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "User with id %d was not found", userId)));
        education.setUser(user);

        return educationMapper.toDto(educationRepository.save(education));
    }

    @Override
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        long educationId = educationDto.getId();

        validateYear(educationDto);

        if (!educationRepository.existsById(educationId)) {
            throw new EntityNotFoundException(String.format(
                    "Education by id %d was not found!", educationId));
        }

        Education education = educationMapper.toEntity(educationDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "User with id %d was not found", userId)));
        education.setUser(user);

        return educationMapper.toDto(educationRepository.save(education));
    }

    @Override
    public EducationDto getById(long educationId) {
        return educationRepository.findById(educationId)
                .map(educationMapper::toDto)
                .orElseThrow(() -> new
                EntityNotFoundException(String.format(
                        "Education with id %d was not found", educationId)));
    }

    private void validateYear(EducationDto educationDto) {
        if (educationDto.getYearFrom() >= LocalDate.now().getYear()) {
            throw new DataValidationException("Year of start must be early");
        }
    }
>>>>>>>> kelpie-master-stream10:src/main/java/school/faang/user_service/service/education/EducationServiceImpl.java
}
