package school.faang.user_service.service.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.EducationDto.AddEducationDto;
import school.faang.user_service.dto.EducationDto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.common.DataValidationException;
import school.faang.user_service.mapper.education.EducationMapperImpl;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {

    private final long USER_ID = 1L;
    private final long EDUCATION_ID = 2L;
    private User user ;
    private AddEducationDto addEducationDto;
    private Education education;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Spy
    private EducationMapperImpl educationMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private EducationService educationService;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(USER_ID);

        addEducationDto = new AddEducationDto();
        education = new Education();
    }

    @Test
    public void testAddEducationWithGreaterYear() {

        addEducationDto.setYearFrom(2026);

        assertThrows(DataValidationException.class, () -> educationService.addEducation(addEducationDto));
    }

    @Test
    public void testAddEducationWhenUserNotFound() {

        addEducationDto.setYearFrom(2023);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> educationService.addEducation(addEducationDto));
    }

    @Test
    public void testAddEducationCurrent() {

        addEducationDto.setYearFrom(2024);
        User user = new User();
        EducationDto educationDto = new EducationDto();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(educationMapper.toEducation(addEducationDto)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.addEducation(addEducationDto);

        assertNotNull(result);
        assertEquals(educationDto, result);
        verify(userRepository).findById(USER_ID);
        verify(educationRepository).save(education);
    }

    @Test
    public void testGetEducationById() {

        EducationDto educationDto = new EducationDto();

        when(educationRepository.findById(EDUCATION_ID)).thenReturn(Optional.of(education));
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto actualDto = educationService.getById(EDUCATION_ID);

        assertNotNull(actualDto);
        assertEquals(educationDto, actualDto);

        verify(educationRepository, times(1)).findById(EDUCATION_ID);
        verify(educationMapper, times(1)).toEducationDto(education);
    }

    @Test
    public void testGetEducationByIdNotFound() {

        when(educationRepository.findById(EDUCATION_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> educationService.getById(EDUCATION_ID));
    }

    @Test
    public void testUpdateEducationWithGreaterYear() {

        Education newEducationData = new Education();
        newEducationData.setYearFrom(2026);

        assertThrows(DataValidationException.class,
                () -> educationService.updateEducation(EDUCATION_ID, newEducationData));
    }

    @Test
    public void testUpdateEducationWhenEducationNotFound() {

        when(educationRepository.findById(EDUCATION_ID))
                .thenThrow(new DataValidationException("Education not Found " + EDUCATION_ID));

        assertThrows(DataValidationException.class, () -> educationService.getById(EDUCATION_ID));
        verify(educationRepository).findById(EDUCATION_ID);
    }

    @Test
    public void testUpdateEducationWithCurrentParameters() {

        long educationId = 1L;
        long currentUserId = 100L;

        Education existingEducation = new Education();
        User user = new User();
        user.setId(currentUserId);
        existingEducation.setUser(user);
        existingEducation.setYearFrom(2011);
        existingEducation.setYearTo(2014);
        existingEducation.setInstitution("MGU");
        existingEducation.setEducationLevel("Low");
        existingEducation.setSpecialization("Math Professor");

        Education newEducationData = new Education();
        newEducationData.setYearFrom(2011);
        newEducationData.setYearTo(2015);
        newEducationData.setInstitution("MADI");
        newEducationData.setEducationLevel("Medium");
        newEducationData.setSpecialization("Language Professor");

        when(educationRepository.findById(educationId)).thenReturn(Optional.of(existingEducation));
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Education updatedEducation = educationService.updateEducation(educationId, newEducationData);

        assertNotNull(updatedEducation);
        assertEquals(2011, updatedEducation.getYearFrom());
        assertEquals(2015, updatedEducation.getYearTo());
        assertEquals("MADI", updatedEducation.getInstitution());
        assertEquals("Medium", updatedEducation.getEducationLevel());
        assertEquals("Language Professor", updatedEducation.getSpecialization());

        verify(educationRepository, times(1)).findById(educationId);
        verify(userContext, times(1)).getUserId();
        verify(educationRepository, times(1)).save(updatedEducation);
    }
}

