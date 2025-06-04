package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user2 = new User();
        user2.setId(2L);

        userDto1 =  UserDto.builder().id(1L).username("ira").email("ira@gmail.com").build();
        userDto2 =  UserDto.builder().id(2L).username("kira").email("kira@gmail.com").build();
    }

    @Test
    void testGetUsersByIds_whenAllIdsValid_thenReturnAllUserDto() {
        List<Long> ids = List.of(1L, 2L);

        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.getUsersByIds(ids);
        assertEquals(List.of(userDto1, userDto2), result);
    }

    @Test
    void testGetUsersByIds_whenSomeIdsNotFound_thenReturnOnlyTwoUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);

        User user3 = new User();
        user3.setId(3L);
        UserDto userDto3 = UserDto.builder().id(3L).username("kira").email("kira@mail.com").build();

        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user3));
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user3)).thenReturn(userDto3);

        List<UserDto> result = userService.getUsersByIds(ids);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(user -> user.getId() == 1L));
        assertTrue(result.stream().anyMatch(user -> user.getId() == 3L));
    }
}
