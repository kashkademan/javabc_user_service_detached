package school.faang.user_service.service.education;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mappers.EducationMapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;

@Service
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Autowired
    public EducationService(UserRepository userRepository, EducationRepository educationRepository,
                            EducationMapper educationMapper) {
        this.userRepository = userRepository;
        this.educationRepository = educationRepository;
        this.educationMapper = educationMapper;
    }

    public EducationDto addEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() > Year.now().getValue()) {
            throw new DataValidationException("YearFrom cannot be greater than the current year.");
        }

        var user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User not found"));

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);

        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() > Year.now().getValue()) {
            throw new DataValidationException("YearFrom cannot be greater than the current year.");
        }

        Education education = educationRepository.findById(educationDto.getId())
                .orElseThrow(() -> new DataValidationException("Education not found"));

        if (education.getUser().getId() != userId) {
            throw new DataValidationException("User does not have permission to update this education record.");
        }

        education = educationMapper.toEducation(educationDto);
        education.setUser(education.getUser());

        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    public EducationDto getById(long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Education not found"));
        return educationMapper.toEducationDto(education);
    }
}