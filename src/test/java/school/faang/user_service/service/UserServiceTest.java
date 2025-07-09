package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.utils.Utils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Spy
    private Utils utils;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("testing get existing user entity by id")
    public void testGetExistUserById() {
        Long userId = 1L;
        User user = getUser(userId);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        User resultUser = userService.getUserById(userId);

        assertNotNull(resultUser);
        assertEquals("Name1", resultUser.getUsername());
    }

    @Test
    @DisplayName("testing get NOT existing user entity by id")
    public void testGetAnAbsentUserById() {
        Long userId = 1L;
        String expected = utils.format(UserService.USER_NOT_FOUND, userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException result = assertThrows(
                UserNotFoundException.class, () -> userService.getUserById(1L));
        assertEquals(expected, result.getMessage());
    }

    @Test
    public void testGetResieveUserDtoById() {
        Long userId = 1L;
        User user = getUser(userId);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        UserResponseDto resultUser = userService.getUserDtoById(userId);

        assertNotNull(resultUser);
        assertEquals(1L, resultUser.id());
        assertEquals("Name1", resultUser.username());
    }

    @Test
    public void testGetExistsUserByIds() {
        List<Long> userIds = List.of(1L, 2L);
        List<User> users = List.of(getUser(1L), getUser(2L));
        when(userRepository.findByIds(userIds)).thenReturn(users);

        List<User> resultList = userService.getUsersByIds(userIds);

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
    }

    @Test
    public void testGetExistsUserDtoByIds() {
        List<Long> userIds = List.of(1L, 2L);
        List<User> users = List.of(getUser(1L), getUser(2L));
        when(userRepository.findByIds(userIds)).thenReturn(users);

        List<UserResponseDto> resultList = userService.getUsersDtoByIds(userIds);

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
    }



    private User getUser(Long userId) {
        return User.builder()
                .id(userId)
                .username("Name%d".formatted(userId))
                .build();
    }
}