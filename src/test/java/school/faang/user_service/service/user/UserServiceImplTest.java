package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.user.UserExperienceFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNamePatternFilter;
import school.faang.user_service.filter.user.UserPhonePatternFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private final UserFiltersDto userFiltersDto
            = new UserFiltersDto("Anton", "89991231213", 3, 7);
    private final UserFilter userExperienceFilter = new UserExperienceFilter();
    private final UserFilter userNamePatternFilter = new UserNamePatternFilter();
    private final UserFilter userPhonePatternFilter = new UserPhonePatternFilter();

    private final User firstUser = User.builder()
            .id(22L)
            .username("antony")
            .build();
    private final User secondUser = User.builder()
            .id(23L)
            .username("bobik")
            .build();

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private List<UserFilter> userFilters;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, countryRepository, userMapper, userContext,
                List.of(userExperienceFilter, userNamePatternFilter, userPhonePatternFilter));
    }

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
        Assertions.assertTrue(userService.getUsersByIds(
                        new ArrayList<>(List.of(UserDto.builder().id(1L).build(), UserDto.builder().id(2L).build())))
                .isEmpty());
    }

    @Test
    void testGetUsersByIds() {
        final List<UserDto> userDtoList = Stream.of(firstUser, secondUser)
                .map(userMapper::toUserDto)
                .toList();
        final List<Long> usersIds = userDtoList.stream().map(UserDto::id).toList();
        when(userRepository.findAllById(usersIds))
                .thenReturn(List.of(firstUser, secondUser));

        List<UserDto> actualUsers = userService.getUsersByIds(userDtoList);
        List<UserDto> expectedUsers = new ArrayList<>(List.of(firstUser, secondUser)).stream()
                .map(userMapper::toUserDto)
                .toList();

        Assertions.assertNotNull(actualUsers);
        Assertions.assertFalse(actualUsers.isEmpty());
        Assertions.assertTrue(actualUsers.containsAll(expectedUsers));
    }


    @Test
    void testGetPremiumUsersPositive() {
        User correctUserWithMinExp = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMin())
                .build();

        User correctUserWithMaxExp = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongNameUser = User.builder()
                .username("Nikolay")
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongPhoneUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone("111111111")
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongExpUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(8)
                .build();

        Mockito.when(userRepository.findPremiumUsers()).thenReturn(Stream.of(correctUserWithMinExp,
                correctUserWithMaxExp, wrongNameUser, wrongPhoneUser, wrongExpUser));

        List<UserDto> expectedPremiumUsers = Arrays.asList(correctUserWithMinExp, correctUserWithMaxExp)
                .stream()
                .map(userMapper::toUserDto)
                .toList();
        List<UserDto> actualPremiumUsers = userService.getPremiumUsers(userFiltersDto);

        Assertions.assertEquals(2, actualPremiumUsers.size());
        Assertions.assertTrue(actualPremiumUsers.containsAll(expectedPremiumUsers));
    }

    @Test
    void testGetPremiumUsersContainsCorrectWords() {
        User wrongNameUser = User.builder()
                .username(userFiltersDto.namePattern() + "k")
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongPhoneUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern() + "2")
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongExpUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax() + 1)
                .build();

        Mockito.when(userRepository.findPremiumUsers())
                .thenReturn(Stream.of(wrongNameUser, wrongPhoneUser, wrongExpUser));

        List<UserDto> actualPremiumUsers = userService.getPremiumUsers(userFiltersDto);

        Assertions.assertEquals(0, actualPremiumUsers.size());
    }
}