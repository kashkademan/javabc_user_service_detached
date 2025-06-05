package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserService userService;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private final UserMapper userMapper;

    public EducationDto addEducation(long userId, EducationDto educationDto) {
        validate(educationDto);
        UserDto userDto = userService.getUserById(userId);
        User user = userMapper.toEntity(userDto);
        Education education = educationMapper.toEntity(educationDto);
        user.getEducation().add(education);
        education.setUser(user);
        educationRepository.save(education);
        return educationMapper.toDto(education);
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        validate(educationDto);
        User user = educationRepository.findById(educationDto.id()).get().getUser();
        if (!user.getId().equals(userId)) {
            throw new DataValidationException("Inappropriate user id");
        }
        Education updatedEducation = educationMapper.toEntity(educationDto);
        updatedEducation.setUser(user);
        educationRepository.save(updatedEducation);
        return educationMapper.toDto(updatedEducation);
    }

    public EducationDto getById(long educationId) {
        return educationRepository.findById(educationId)
                .map(educationMapper::toDto)
                .orElseThrow(() -> new DataValidationException(
                        "The education with id = " + educationId + "does not exist"));
    }

    private void validate(EducationDto educationDto) {
        if (educationDto.yearFrom() > LocalDateTime.now().getYear()) {
            throw new DataValidationException("Year from can not be later than current year");
        }
    }
}
