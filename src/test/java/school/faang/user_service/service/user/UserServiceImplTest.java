package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private final User firstUser = User.builder()
            .id(22L)
            .username("antony")
            .build();
    private final User secondUser = User.builder()
            .id(23L)
            .username("bobik")
            .build();

    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void testGetUserThrowsEntityNotFound() {
        when(userRepository.getByIdOrThrow(anyLong())).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void testGetUser() {
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        UserDto actualUser = userService.getById(firstUser.getId());

        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(firstUser.getId(), actualUser.id());
        Assertions.assertEquals(firstUser.getUsername(), actualUser.username());
    }

    @Test
    void testGetUsersByIdsReturnEmptyListIfEmptyArgument() {
        Assertions.assertTrue(userService.getUsersByIds(new ArrayList<>()).isEmpty());
    }

    @Test
    void testGetUsersByIdsReturnEmptyListIfUsersNotFound() {
        Assertions.assertTrue(userService.getUsersByIds(new ArrayList<>(List.of(1L, 2L))).isEmpty());
    }

    @Test
    void testGetUsersByIds() {
        final List<Long> usersIds = List.of(firstUser.getId(), secondUser.getId());
        when(userRepository.findAllById(usersIds))
                .thenReturn(List.of(firstUser, secondUser));

        List<UserDto> actualUsers = userService.getUsersByIds(usersIds);
        List<UserDto> expectedUsers = new ArrayList<>(List.of(firstUser, secondUser)).stream()
                .map(userMapper::toUserDto)
                .toList();

        Assertions.assertNotNull(actualUsers);
        Assertions.assertFalse(actualUsers.isEmpty());
        Assertions.assertTrue(actualUsers.containsAll(expectedUsers));
    }
}