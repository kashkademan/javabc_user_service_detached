package school.faang.user_service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.NotSupportedDataException;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.service.education.impl.EducationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {

    @Spy
    private UserContext userContext;

    @Mock
    private EducationRepository educationRepository;

    @InjectMocks
    private EducationServiceImpl educationService;

    @BeforeEach
    public void init() {
        userContext.setUserId(1);
    }

    @Test
    public void testYearFromIsNull() {
        Assert.assertThrows(NullPointerException.class,
                () -> educationService.addEducation(1, new EducationDto(null, 2025,
                        "intitute1", "middle", "spec1")));
    }

    @Test
    public void testYearFromIsInvalid() {
        Assert.assertThrows(NotSupportedDataException.class,
                () -> educationService.addEducation(1, new EducationDto(2030, 2025,
                        "intitute1", "middle", "spec1")));
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
}
