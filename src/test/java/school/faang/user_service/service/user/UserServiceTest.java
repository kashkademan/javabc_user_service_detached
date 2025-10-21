package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.filter.user.UserExperienceFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNamePatternFilter;
import school.faang.user_service.filter.user.UserPhonePatternFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final UserFiltersDto userFiltersDto
            = new UserFiltersDto("Anton", "89991231213", 3, 7);
    private final UserFilter userExperienceFilter = new UserExperienceFilter();
    private final UserFilter userNamePatternFilter = new UserNamePatternFilter();
    private final UserFilter userPhonePatternFilter = new UserPhonePatternFilter();

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Spy
    private UserMapperImpl userMapper;
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