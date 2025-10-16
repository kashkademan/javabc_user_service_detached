package school.faang.user_service.service.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.EducationCreateDto;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.dto.education.EducationUpdateDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {
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

    private User user;
    private Education education;
    private EducationDto educationDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test_user");

        education = new Education(
                10L,
                2000,
                2005,
                "University",
                "Bachelor",
                "Computer Science",
                user);

        educationDto = new EducationDto(
                10L,
                2000,
                2005,
                "University",
                "Bachelor",
                "Computer Science"
        );
    }

    @Test
    void addEducationShouldAddSuccessfully() {
        EducationCreateDto createDto = new EducationCreateDto(
                2000,
                2005,
                "University",
                "Bachelor",
                "Computer Science"
        );

        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);
        when(educationMapper.toEducation(createDto)).thenReturn(education);
        when(educationRepository.save(any(Education.class))).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.addEducation(createDto);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(educationDto, result, "Возвращенный DTO должен соответствовать ожидаемому");
        verify(educationRepository).save(any(Education.class));
        verify(educationMapper).toEducation(createDto);
        verify(educationMapper).toEducationDto(education);
    }

    @Test
    void updateEducationShouldUpdateSuccessfully() {
        EducationUpdateDto updateDto = new EducationUpdateDto(
                2001,
                2006,
                "Updated University",
                "Master",
                "Software Engineering"
        );

        when(userContext.getUserId()).thenReturn(1L);
        when(educationRepository.getByIdOrThrow(10L)).thenReturn(education);
        doNothing().when(educationMapper).updateEducationFromDto(updateDto, education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.updateEducation(10L, updateDto);

        assertThat(result).isNotNull();
        verify(educationMapper).updateEducationFromDto(updateDto, education);
        verify(educationRepository).save(education);
    }

    @Test
    void getByIdShouldReturnEducationWhenUserIsOwner() {
        when(userContext.getUserId()).thenReturn(1L);
        when(educationRepository.getByIdOrThrow(10L)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.getById(10L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        verify(educationRepository).getByIdOrThrow(10L);
    }

    @Test
    void getByIdShouldThrowWhenUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        education.setUser(anotherUser);

        when(userContext.getUserId()).thenReturn(1L);
        when(educationRepository.getByIdOrThrow(10L)).thenReturn(education);

        assertThatThrownBy(() -> educationService.getById(10L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Не достаточно прав");

        verify(educationRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteEducationShouldDeleteSuccessfully() {
        when(userContext.getUserId()).thenReturn(1L);
        when(educationRepository.getByIdOrThrow(10L)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.deleteEducation(10L);

        assertThat(result).isNotNull();
        verify(educationRepository).deleteById(10L);
    }

    @Test
    void deleteEducationShouldThrow_WhenNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("Another");
        education.setUser(anotherUser);

        when(userContext.getUserId()).thenReturn(1L);
        when(educationRepository.getByIdOrThrow(10L)).thenReturn(education);

        assertThatThrownBy(() -> educationService.deleteEducation(10L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Не достаточно прав");

        verify(educationRepository, never()).deleteById(anyLong());
    }
}

