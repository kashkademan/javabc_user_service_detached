package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void findUserById_findsWithRightUserId() {
        User user = new User();
        user.setId(1L);
        Optional<User> userOptional = Optional.of(user);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(userRepository.findById(user.getId())).thenReturn(userOptional);

        Optional<User> actualUserOptional = userRepository.findById(user.getId());

        verify(userRepository, times(1)).findById(userIdCaptor.capture());
        Long userIdCaptorValue = userIdCaptor.getValue();
        assertEquals(user.getId(), userIdCaptorValue);
        assertTrue(actualUserOptional.isPresent());
        assertEquals(userOptional.get(), actualUserOptional.get());
    }

    @Test
    void findUserById_notFindsWithWrongUserId() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(GoalDataException.class, () -> userService.findUserById(user.getId()));
    }


    @Test
    void saveUser() {
        User user = new User();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userValue = userCaptor.getValue();
        assertEquals(user, userValue);
    }

    @Test
    void testGetUserNotFound() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(user.getId()));
    }

    @Test
    void testGetUserFound() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getUser(user.getId());
        UserDto expectedUserDto = userMapper.toDto(user);
        assertEquals(expectedUserDto, actualUserDto);
        assertEquals(1L, actualUserDto.getId());
    }

    @Test
    void testGetUsersByIdsNotFound() {
        when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of());

        assertThrows(UserNotFoundException.class, () -> userService.getUsersByIds(List.of(1L, 2L)));
    }

    @Test
    void testGetUsersByIdsFound() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<User> users = new ArrayList<>();
        for (Long id : ids) {
            User user = User.builder().id(id).build();
            users.add(user);
        }
        when(userRepository.findAllById(ids)).thenReturn(users);

        List<UserDto> actualUserDtos = userService.getUsersByIds(ids);
        List<UserDto> expectedUserDtos = userMapper.toDtos(users);
        assertEquals(expectedUserDtos, actualUserDtos);
        assertEquals(1L, actualUserDtos.get(0).getId());
    }
}