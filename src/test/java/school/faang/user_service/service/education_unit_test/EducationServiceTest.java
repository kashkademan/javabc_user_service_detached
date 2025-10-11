package school.faang.user_service.service.education_unit_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.education.EducationService;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private EducationMapper educationMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private EducationService educationService;

    private CreateEducationDto createEducationDto;
    private User user;
    private Education education;
    private EducationDto educationDto;
    private Education existingEducation;
    private Education updatedEducation;
    private UpdateEducationDto updateEducationDto;

    private final long USER_ID = 1L;
    private final long EDUCATION_ID = 1L;

    @BeforeEach
    void setUp() {
        createEducationDto = new CreateEducationDto(
                2020, 2024, "University", "Bachelor", "Computer Science");

        existingEducation = new Education();
        existingEducation.setId(EDUCATION_ID);

        user = new User();
        user.setId(USER_ID);
        existingEducation.setUser(user);

        education = new Education();
        education.setId(EDUCATION_ID);

        updatedEducation = new Education();
        updatedEducation.setId(EDUCATION_ID);
        updatedEducation.setUser(user);

        educationDto = new EducationDto(EDUCATION_ID, 2020, 2024, "University",
                "Bachelor", "Computer Science");

        updateEducationDto = new UpdateEducationDto(
                2020, 2024, "University 123123", "Bachelor", "Computer Science");

        lenient().when(userContext.getUserId()).thenReturn(USER_ID);
        lenient().when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);
        lenient().when(educationMapper.toEducation(createEducationDto)).thenReturn(education);
        lenient().when(educationRepository.save(education)).thenReturn(education);
        lenient().when(educationMapper.toEducationDto(education)).thenReturn(educationDto);
        lenient().when(educationRepository.getByIdOrThrow(EDUCATION_ID)).thenReturn(existingEducation);
        lenient().doNothing().when(educationMapper).updateEducationFromDto(updateEducationDto, existingEducation);
        lenient().when(educationRepository.save(existingEducation)).thenReturn(updatedEducation);
        lenient().when(educationMapper.toEducationDto(updatedEducation)).thenReturn(educationDto);
    }

    @Test
    public void testGetUserId() {
        long userId = userContext.getUserId();
        assertEquals(USER_ID, userId, "User ID должен быть равен " + USER_ID);
    }

    @Test
    void testValidateYearFrom() {
        // Проверка, что метод validateYearFrom вызывается без ошибок
        assertDoesNotThrow(() -> educationService.validateYearFrom(createEducationDto.yearFrom()),
                "Метод validateYearFrom не должен выбрасывать исключение");
    }

    @Test
    void testValidateYearFromFutureYear() {

        int futureYear = Year.now().getValue() + 1;

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            educationService.validateYearFrom(futureYear);
        });

        assertEquals("Год начала обучения не может быть больше текущего", exception.getMessage());
    }

    @Test
    void testGetByIdOrThrow() {
        User fetchedUser = userRepository.getByIdOrThrow(USER_ID);
        assertNotNull(fetchedUser);
        assertEquals(USER_ID, fetchedUser.getId());
    }

    @Test
    void testToEducation() {

        Education mappedEducation = educationMapper.toEducation(createEducationDto);
        assertNotNull(mappedEducation);
        assertEquals(mappedEducation, education);
    }

    @Test
    void testToEducationDto() {
        EducationDto mappedEducationDto = educationMapper.toEducationDto(education);
        assertNotNull(mappedEducationDto);
        assertEquals(mappedEducationDto, educationDto);
    }

    @Test
    void testSave() {
        Education savedEducation = educationRepository.save(education);
        assertNotNull(savedEducation);
        assertEquals(savedEducation, education);
    }

    @Test
    void testAddEducation() {

        EducationDto result = educationService.addEducation(createEducationDto);

        verify(userContext).getUserId();
        verify(userRepository).getByIdOrThrow(USER_ID);
        verify(educationMapper).toEducation(createEducationDto);
        verify(educationRepository).save(education);
        verify(educationMapper).toEducationDto(education);

        assertNotNull(result);
        assertEquals(result, educationDto);
    }

    @Test
    void testGetByIdOrThrowUpdate() {

        Education fetched = educationRepository.getByIdOrThrow(EDUCATION_ID);

        assertNotNull(fetched);
        assertEquals(EDUCATION_ID, fetched.getId());
    }

    @Test
    void testValidateUserIsEducationOwner() {
        Education anotherEducation = new Education();

        User anotherUser = new User();
        anotherUser.setId(123L);
        anotherEducation.setUser(anotherUser);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () ->
                educationService.validateUserIsEducationOwner(USER_ID, anotherEducation));

        assertEquals("Не достаточно прав для получения этих данных", ex.getMessage());
    }

    @Test
    void shouldUpdateEducationEntity() {
        educationService.updateEducation(EDUCATION_ID, updateEducationDto);

        verify(educationMapper).updateEducationFromDto(updateEducationDto, existingEducation);
        verify(educationRepository).save(existingEducation);
    }

    @Test
    void shouldReturnUpdatedEducationDto() {
        EducationDto result = educationService.updateEducation(EDUCATION_ID, updateEducationDto);

        verify(educationMapper).toEducationDto(updatedEducation);
        assertNotNull(result);
        assertEquals(educationDto, result);
    }

    @Test
    void shouldPerformFullUpdateFlow() {
        EducationDto result = educationService.updateEducation(EDUCATION_ID, updateEducationDto);

        verify(userContext).getUserId();
        verify(educationRepository).getByIdOrThrow(EDUCATION_ID);
        verify(educationMapper).updateEducationFromDto(updateEducationDto, existingEducation);
        verify(educationRepository).save(existingEducation);
        verify(educationMapper).toEducationDto(updatedEducation);

        assertNotNull(result);
        assertEquals(educationDto, result);
    }
}
