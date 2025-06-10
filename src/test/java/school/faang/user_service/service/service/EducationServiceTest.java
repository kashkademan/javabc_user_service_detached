package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapperImpl;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.education.EducationService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {
    private static final long ID = 1;
    @Mock
    private UserService userService;

    @Mock
    private EducationRepository educationRepository;

    @Spy
    private EducationMapperImpl educationMapper;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private EducationService educationService;

    @Test
    public void dataValidationExceptionTest() {
        EducationDto badEducationDto = new EducationDto(ID, 2026, 2027, "", "", "");
        assertThrows(DataValidationException.class, () -> educationService.addEducation(ID, badEducationDto));
    }

    @Test
    public void addEducationAdds() {
        UserDto userDto = new UserDto(ID, "name", "email");
        User user = userMapper.toEntity(userDto);
        user.setEducation(new ArrayList<>());
        when(userService.getUserById(ID)).thenReturn(userDto);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        EducationDto educationDto = new EducationDto(ID, 2023, 2027, "", "", "");
        assertEquals(educationService.addEducation(ID, educationDto), educationDto);
    }

    @Test
    public void inappropriateUserId() {
        EducationDto educationDto = new EducationDto(ID, 2023, 2027, "", "", "");
        User user = new User();
        user.setId(ID);
        when(educationRepository.findById(educationDto.id()))
                .thenReturn(Optional.of(new Education(ID, 2023, 2027, "", "", "", user)));
        assertThrows(DataValidationException.class, () -> educationService.updateEducation(2, educationDto));
    }

    @Test
    public void updateEducationUpdates() {
        EducationDto educationDto = new EducationDto(ID, 2023, 2027, "", "", "");
        User user = new User();
        user.setId(ID);
        when(educationRepository.findById(educationDto.id()))
                .thenReturn(Optional.of(new Education(ID, 2023, 2027, "", "", "", user)));

        assertEquals(educationDto, educationService.updateEducation(ID, educationDto));
    }

    @Test
    public void wrongId() {
        when(educationRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> educationService.getById(ID));
    }

    @Test
    public void getByIdWorks() {
        when(educationRepository.findById(ID)).thenReturn(Optional.of(new Education()));
        assertNotNull(educationService.getById(ID));
    }
}
