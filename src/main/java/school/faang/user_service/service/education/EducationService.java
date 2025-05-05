package school.faang.user_service.service.education;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.mapper.EducationMapper;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Autowired
    public EducationService(UserRepository userRepository, EducationRepository educationRepository, EducationMapper educationMapper) {
        this.userRepository = userRepository;
        this.educationRepository = educationRepository;
        this.educationMapper = educationMapper;
    }

    public void checkYearFrom(EducationDto educationDto) throws DataValidationException {
        if (educationDto.getYearFrom() > LocalDate.now().getYear()) {
            throw new DataValidationException("Your year is greater than the current year.");
        }//The task is to check if the yearFrom is less than the current year.
    }    //However, the yearFrom can be equal to the current year.

    public EducationDto addEducation(long userId, EducationDto educationDto) throws DataValidationException {
        checkYearFrom(educationDto);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new DataValidationException("User with ID " + userId + " not found");
        }

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(userOptional.get());

        educationRepository.save(education);
        return educationMapper.toEducationDto(education);
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) throws DataValidationException {
        checkYearFrom(educationDto);
        return null;
    }
}
