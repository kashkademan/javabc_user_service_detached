package school.faang.user_service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.education.EducationService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @InjectMocks
    private EducationService educationService;

    @Test
    public void testNullYearFromIsInvalid() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> educationService.addEducation(1, new EducationDto(null, 2025,
                        "intitute1", "middle", "spec1")));
    }

    @Test
    public void testAddEducationWithBlankInstitution() {
        EducationDto dto = new EducationDto();
        dto.setInstitution(" ");

        assertThrows(DataValidationException.class, () -> educationService.addEducation(1, dto));
    }

    @Test
    public void testEmptyInstitutionIsInvalid() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> educationService.addEducation(1, new EducationDto(2020, 2025,
                        " ", "middle", "spec1")));
    }

    @Test
    public void testAddEducationWithExistingInstitution() {
        EducationDto dto = prepareData(true);

        assertThrows(DataValidationException.class, () -> educationService.addEducation(1, dto));
    }

    @Test
    public void testSaveEducation() {
        EducationDto dto = prepareData(false);
    }

    @Test
    public void testEducationIsSaved() {
        Education education = new Education(1L, 2020, 2025, "inst1",
                "middle", "spec1", new User());

        Mockito.when(educationRepository.save(education)).thenReturn(new Education());
        educationRepository.save(education);
        Mockito.verify(educationRepository, Mockito.times(1))
                .save(education);
    }

    private EducationDto prepareData(boolean existsByInstitution) {
        EducationDto dto = new EducationDto();
        dto.setInstitution(" ");
        when(EducationRepository.existsByInstitution(dto.getInstitution())).thenReturn(false);
        return dto;
    }
}
