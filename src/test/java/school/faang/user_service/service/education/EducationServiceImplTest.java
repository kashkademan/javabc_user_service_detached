package school.faang.user_service.service.education;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.user.education.CreateEducationDto;
import school.faang.user_service.dto.user.education.EducationDto;
import school.faang.user_service.dto.user.education.UpdateEducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EducationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationServiceImpl educationService;

    private AutoCloseable mocks;

    @BeforeEach
    void setup() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }


    @Test
    void addEducationSuccess() {
        CreateEducationDto dto = new CreateEducationDto(
                2020, 2022, "SSAU", "Entry", "CS"
        );
        User user = createUser(10L);
        Education education = createEducation(100L, user);
        EducationDto expectedDto = new EducationDto(100L, 2020, 2022, "SSAU", "Entry", "CS");

        when(userRepository.getByIdOrThrow(10L)).thenReturn(user);
        when(educationMapper.toEducation(dto)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(expectedDto);

        EducationDto result = educationService.addEducation(10L, dto);

        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.institution(), result.institution());
        assertEquals(expectedDto.educationLevel(), result.educationLevel());
        assertEquals(expectedDto.yearFrom(), result.yearFrom());
        assertEquals(expectedDto.yearTo(), result.yearTo());
        assertEquals(expectedDto.specialization(), result.specialization());

        verify(educationRepository, times(1)).save(education);
    }

    @Test
    void addEducationInvalidYearFrom() {
        CreateEducationDto dto = new CreateEducationDto(
                2100, 2022, "SSAU", "Entry", "CS"
        );
        assertThrows(DataValidationException.class, () -> educationService.addEducation(10L, dto));
    }


    @Test
    void updateEducationSuccess() {
        UpdateEducationDto dto = new UpdateEducationDto(
                2010, 2014, "MGU", "Master", "AI"
        );
        User user = createUser(10L);
        Education oldEd = createEducation(500L, user);
        EducationDto expectedDto = new EducationDto(500L, 2010, 2014, "MGU", "Master", "AI");

        when(educationRepository.findById(500L)).thenReturn(Optional.of(oldEd));
        doNothing().when(educationMapper).updateEducationFromDto(dto, oldEd);
        when(educationRepository.save(oldEd)).thenReturn(oldEd);
        when(educationMapper.toEducationDto(oldEd)).thenReturn(expectedDto);

        EducationDto result = educationService.updateEducation(10L, 500L, dto);

        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.institution(), result.institution());
        assertEquals(expectedDto.educationLevel(), result.educationLevel());
        assertEquals(expectedDto.yearFrom(), result.yearFrom());
        assertEquals(expectedDto.yearTo(), result.yearTo());
        assertEquals(expectedDto.specialization(), result.specialization());

        verify(educationRepository, times(1)).save(oldEd);
        verify(educationMapper, times(1)).updateEducationFromDto(dto, oldEd);
    }

    @Test
    void updateEducationInvalidYearFrom() {
        UpdateEducationDto dto = new UpdateEducationDto(
                2100, null, null, null, null
        );
        Education oldEd = createEducation(1L, createUser(10L));

        when(educationRepository.findById(1L)).thenReturn(Optional.of(oldEd));

        assertThrows(DataValidationException.class, () -> educationService.updateEducation(10L, 1L, dto));
    }

    @Test
    void updateEducationNotFound() {
        when(educationRepository.findById(5L)).thenReturn(Optional.empty());
        UpdateEducationDto dto = new UpdateEducationDto(
                2000, 2004, "Inst", "Lvl", "Spec"
        );

        assertThrows(EntityNotFoundException.class, () -> educationService.updateEducation(10L, 5L, dto));
    }

    @Test
    void updateEducationForbidden() {
        UpdateEducationDto dto = new UpdateEducationDto(
                2000, 2004, "Inst", "Lvl", "Spec"
        );
        User owner = createUser(99L);
        Education wrongEd = createEducation(1L, owner);

        when(educationRepository.findById(1L)).thenReturn(Optional.of(wrongEd));

        assertThrows(ForbiddenException.class, () -> educationService.updateEducation(10L, 1L, dto));
    }


    @Test
    void getByIdSuccess() {
        User user = createUser(10L);
        Education ed = createEducation(100L, user);
        EducationDto expectedDto = new EducationDto(100L, null, null, null, null, null);

        when(educationRepository.getByIdOrThrow(100L)).thenReturn(ed);
        when(educationMapper.toEducationDto(ed)).thenReturn(expectedDto);

        EducationDto result = educationService.getById(100L);

        assertEquals(expectedDto.id(), result.id());
    }

    @Test
    void getByIdNotFound() {
        when(educationRepository.getByIdOrThrow(500L))
                .thenThrow(new EntityNotFoundException("Not found"));

        assertThrows(EntityNotFoundException.class, () -> educationService.getById(500L));
    }


    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Education createEducation(Long id, User user) {
        Education ed = new Education();
        ed.setId(id);
        ed.setUser(user);
        return ed;
    }
}