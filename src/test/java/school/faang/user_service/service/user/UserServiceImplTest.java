package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.filter.Filter;
import school.faang.user_service.service.filter.FilterService;
import school.faang.user_service.service.filter.user.UserAboutMeContainsFilter;
import school.faang.user_service.service.filter.user.UserEmailContainsFilter;
import school.faang.user_service.service.filter.user.UserFilterServiceImpl;
import school.faang.user_service.service.filter.user.UserPhoneFilter;
import school.faang.user_service.service.filter.user.UserUsernameContainsFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserUsernameContainsFilter usernameContainsFilter = new UserUsernameContainsFilter();
    private UserEmailContainsFilter emailContainsFilter = new UserEmailContainsFilter();
    private UserPhoneFilter phoneFilter = new UserPhoneFilter();
    private UserAboutMeContainsFilter aboutMeContainsFilter = new UserAboutMeContainsFilter();
    private List<Filter<User, UserFilterDto>> filters
            = List.of(usernameContainsFilter, phoneFilter, emailContainsFilter, aboutMeContainsFilter);
    @Mock
    private UserRepository userRepository;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private CountryRepository countryRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserContext userContext;
    @Spy
    private FilterService<User, UserFilterDto> filterService = new UserFilterServiceImpl(filters);

    @InjectMocks
    private UserServiceImpl service;

    private final UserServiceTestData testData = new UserServiceTestData();

    @Test
    void create_success() {
        var country = testData.getCountry(1L, "Kazakhstan");
        var createDto = testData.getCreateDto("Myrzakhmet", 1L);
        var currentUser = testData.getUser(null, createDto, country);
        var user = testData.getUser(1L, createDto, country);
        var userDto = testData.getViewDto(user);

        when(countryRepository.getByIdOrThrow(country.getId()))
                .thenReturn(country);
        when(userRepository.save(eq(currentUser)))
                .thenReturn(user);

        var actual = service.create(createDto);
        assertEquals(userDto, actual);
    }

    @Test
    void update() {
        var country = testData.getCountry(1L, "America");
        var user = testData.getUser(1L, "Myrzakhmet", country);
        var updateDto = testData.getUpdateDto(user);
        var newUser = userMapper.clone(user);
        userMapper.update(updateDto, newUser);

        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.getByIdOrThrow(user.getId()))
                .thenReturn(user);
        when(countryRepository.getByIdOrThrow(updateDto.countryId()))
                .thenReturn(country);
        when(userRepository.save(eq(newUser)))
                .thenReturn(newUser);

        var expected = userMapper.toUserDto(newUser);
        var actual = service.update(user.getId(), updateDto);
        assertEquals(expected, actual);
    }

    @Test
    void getById() {
        var country = testData.getCountry(1L, "CountryName");
        var user = testData.getUser(1L, "user", country);

        when(userRepository.getByIdOrThrow(user.getId())).thenReturn(user);

        var expected = userMapper.toUserDto(user);
        var actual = service.getById(user.getId());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("school.faang.user_service.service.user.UserServiceTestData#provideParams")
    void getUsers(UserFilterDto filterDto, List<User> users, List<UserDto> expected) {
        if (filterDto.onlyPremium()) {
            when(userRepository.findPremiumUsers())
                    .thenReturn(users.stream());
        } else {
            when(userRepository.findAll()).thenReturn(users);
        }

        var actual = service.getUsers(filterDto);
        assertEquals(expected, actual);
    }
}