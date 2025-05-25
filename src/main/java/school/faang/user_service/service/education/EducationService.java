package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EducationService {
    private UserRepository userRepository;
    private EducationRepository educationRepository;
    private EducationMapper educationMapper;

    public EducationDto addEducation(long userId, EducationDto educationDto) {
        validate(userId, educationDto);
        userRepository.findById(userId).get().getEducation().add(educationMapper.toEntity(educationDto));
        return educationMapper.toDto(educationRepository.save(educationMapper.toEntity(educationDto)));
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        validate(userId, educationDto);
        User user = educationRepository.findById(educationDto.id()).get().getUser();
        if (user.getId() != userId) {
            throw new DataValidationException("Inappropriate user id");
        }
        Education updatedEducation = educationMapper.toEntity(educationDto);
        updatedEducation.setUser(user);
        return educationMapper.toDto(educationRepository.save(updatedEducation));

    }

    public EducationDto getById(long educationId) {
        if (educationRepository.findById(educationId).isEmpty()) {
            throw new DataValidationException("Non existent id");
        }
        return educationMapper.toDto(educationRepository.findById(educationId).get());
    }

    private void validate(long userId, EducationDto educationDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new DataValidationException("Wrong user id");
        }
        if (educationDto.yearFrom() < LocalDateTime.now().getYear()) {
            throw new DataValidationException("Year from can not be later than current year");
        }
    }
}
