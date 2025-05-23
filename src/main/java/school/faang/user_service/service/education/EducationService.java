package school.faang.user_service.service.education;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
public class EducationService {

    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private EducationDto dto;


    public void addEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() >= Year.now().getValue()) {
            throw new DataValidationException("yearFrom должно быть меньше текущего года");
        }

        Optional<User> user = userRepository.findById(userId);

        EducationDto education = null;
        Education entity = educationMapper.toEntity(education);
        entity = educationRepository.save((entity));
        educationMapper.toDto(entity);
    }

    public EducationDto updateEducation(long userId) {
        throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");

    }
}
