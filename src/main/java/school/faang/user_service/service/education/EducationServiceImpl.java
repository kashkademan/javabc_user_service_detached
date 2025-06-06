package school.faang.user_service.service.education;


import jakarta.persistence.EntityNotFoundException;
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
public class EducationServiceImpl implements EducationService {

    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Override
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() >= Year.now().getValue()) {
            throw new DataValidationException("yearFrom must be less than current year");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=%d not found"));


        Education education = educationMapper.toEntity(educationDto);
        education.setUser(user);

        return educationMapper.toDto(educationRepository.save(education));
    }

    @Override
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() == null || educationDto.getYearFrom() > Year.now().getValue()) {
            throw new DataValidationException("YearFrom must be less than current year");
        }

        Optional<Education> existingEducation = educationRepository.findById(educationDto.getId());
        String educationId = "";
        if (educationId.isEmpty()) {
            throw new EntityNotFoundException("Education with id=" + educationId + " not found");
        }

        if (!existingEducation.get().getUser().getId().equals(userId)) {
            throw new DataValidationException("Нельзя обновлять чужие данные");
        }

        User user = existingEducation.get().getUser();
        Education updatedEducation = educationMapper.toEducation(educationDto);
        updatedEducation.setUser(user);

        Education savedEducation = educationRepository.save(updatedEducation);
        return educationMapper.toEducationDto(savedEducation);
    }

    @Override
    public EducationDto getById(long educationId) {
        return educationRepository.findById(educationId)
                .map(educationMapper::toEducationDto)
                .orElseThrow(() -> new DataValidationException("Образование не найдено"));
    }
}