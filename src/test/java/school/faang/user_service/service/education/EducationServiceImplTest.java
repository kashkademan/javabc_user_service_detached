package school.faang.user_service.service.education;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapperImpl;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Spy
    private EducationMapperImpl educationMapper;

    @InjectMocks
    private EducationServiceImpl educationService;

    @Captor
    private ArgumentCaptor<Education> captor;

    @Test
    public void testAddWithInvalidYearFrom() {
        EducationDto dto = prepareDto(true);

        assertThrows(DataValidationException.class,
                () -> educationService.addEducation(1L, dto));
    }

    @Test
    public void testAddSavesEducation() {
        EducationDto dto = prepareDto();
        when(userRepository.getByIdOrThrow(Mockito.anyLong())).thenReturn(new User());

        educationService.addEducation(1L, dto);

        verify(educationRepository, times(1)).save(captor.capture());
        Education education = captor.getValue();
        compareData(dto, education);
    }

    @Test
    public void testUpdateWithInvalidYearFrom() {
        EducationDto dto = prepareDto(true);

        assertThrows(DataValidationException.class,
                () -> educationService.updateEducation(1L, 1L, dto));
    }

    @Test
    public void testUpdateNotHimself() {
        EducationDto dto = prepareDto();
        Education education = prepareEntity(5L);
        when(educationRepository.getByIdOrThrow(Mockito.anyLong()))
                .thenReturn(education);

        assertThrows(ForbiddenException.class,
                () -> educationService.updateEducation(4L, 1L, dto));

    }

    @Test
    public void testUpdateSavesEducation() {
        EducationDto dto = prepareDto();
        Education education = prepareEntity(1L);
        when(educationRepository.getByIdOrThrow(Mockito.anyLong())).thenReturn(education);

        educationService.updateEducation(1L, 1L, dto);

        verify(educationRepository, times(1)).save(captor.capture());
        education = captor.getValue();
        compareData(dto, education);
    }

    @Test
    public void testFoundById() {
        educationService.getById(1L);

        verify(educationRepository, times(1)).getByIdOrThrow(1L);
    }

    private EducationDto prepareDto() {
        return prepareDto(false);
    }

    private EducationDto prepareDto(boolean brakedYear) {
        EducationDto dto;
        int yearFrom = brakedYear ? LocalDate.now().getYear() + 1 : 2022;
        String institution = "URFU";
        dto = new EducationDto(1L, yearFrom, yearFrom + 5,
                institution, "Magister", "Economist");

        return dto;
    }

    private Education prepareEntity(long id) {
        User user = new User();
        user.setUsername("usertest");
        user.setId(id);
        Education education = new Education();
        education.setUser(user);

        return education;
    }

    private void compareData(EducationDto dto, Education entity) {
        assertEquals(dto.institution(), entity.getInstitution());
        assertEquals(dto.yearFrom(), entity.getYearFrom());
        assertEquals(dto.yearTo(), entity.getYearTo());
        assertEquals(dto.educationLevel(), entity.getEducationLevel());
        assertEquals(dto.specialization(), entity.getSpecialization());
    }
}