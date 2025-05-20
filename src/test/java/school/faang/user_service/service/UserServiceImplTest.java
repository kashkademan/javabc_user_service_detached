package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserServiceImpl;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUsersByIds() {
        List<Long> ids = List.of(1L, 2L);

        User user1 = new User(); user1.setId(1L);
        User user2 = new User(); user2.setId(2L);

        UserDto userDto1 = new UserDto(1L, "ira", "ira@mail.com");
        UserDto userDto2 = new UserDto(2L, "kira", "kira@mail.com");

        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.getUsersByIds(ids);
        assertEquals(List.of(userDto1, userDto2), result);
    }

    @Test
    void testGetUsersByIds_shouldReturnOnlyTwoUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);

        User user1 = new User(); user1.setId(1L);
        User user3 = new User(); user3.setId(3L);

        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user3));
        when(userMapper.toUserDto(user1)).thenReturn(new UserDto(1L, "ira", "ira@mail.com"));
        when(userMapper.toUserDto(user3)).thenReturn(new UserDto(3L, "kira", "kira@mail.com"));

        List<UserDto> result = userService.getUsersByIds(ids);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
    }
}
