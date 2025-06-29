package school.faang.user_service.education_addition;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private final EducationValidationService educationValidationService;

    @Override
    public EducationDto addEducation(long userId, EducationDto educationDto) throws DataValidationException {
        educationValidationService.validateOnAdd(userId, educationDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " not found"));

        Education education = educationMapper.toEntity(existingEducation, educationDto);
        education.setUser(user);

        return educationMapper.toDto(educationRepository.save(education));
    }

    @Override
    public EducationDto updateEducation(long userId, long educationId, EducationDto educationDto) throws DataValidationException {
        Education existingEducation = educationRepository.findById(educationDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Education not found"));

        educationValidationService.validateOnUpdate(userId, existingEducation, educationDto);

        educationMapper.toEntity(existingEducation, educationDto);
        return educationMapper.toDto(educationRepository.save(existingEducation));
    }

    @Override
    public EducationDto getById(long educationId) throws DataValidationException {
        educationValidationService.validateOnGetById(educationId);
        return educationRepository.findById(educationId)
                .map(educationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Education not found"));
    }
}