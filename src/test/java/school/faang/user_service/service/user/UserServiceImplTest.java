package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.entity.user.Country;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    private static UserDto userDto;
    private static User user;
    private static Country country;

    @BeforeAll
    static void setUp() {
        userDto = new UserDto(
                1L,
                "JohnDoe",
                "johndoe@example.com",
                "1234567890",
                "About John Doe"
        );
        country = new Country(1L, "America", new ArrayList<>());
        var createDto = new UserCreateDto(
                "JohnDoe",
                "johndoe@example.com",
                "Mega_str0ng_passwd",
                country.getId()
        );
        user = new UserMapperImpl().toUser(createDto);
        user.setId(userDto.id());
        user.setCountry(country);
        user.setPhone(userDto.phone());
        user.setAboutMe(userDto.aboutMe());
    }

    @Test
    void create() {
        var createDto = new UserCreateDto(
                "JohnDoe",
                "johndoe@example.com",
                "Mega_str0ng_passwd",
                country.getId()
        );
        var currentUser = userMapper.toUser(createDto);
        currentUser.setCountry(country);

        when(countryRepository.getByIdOrThrow(country.getId()))
                .thenReturn(country);
        when(userRepository.save(eq(currentUser)))
                .thenReturn(user);

        var actual = service.create(createDto);
        assertEquals(userDto, actual);
    }

    @Test
    void update() {
        var updateDto = new UserUpdateDto(
                user.getUsername() + "2",
                user.getEmail(),
                null,
                null,
                country.getId(),
                "New Yourk"
        );
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
        var userId = user.getId();
        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

        var expected = userMapper.toUserDto(user);
        var actual = service.getById(userId);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> provideParams() {
        var createDto1 = new UserCreateDto(
                "JaneSmith",
                "janesmith@example.com",
                "Mega_str0ng_passwd2",
                1L
        );
        var createDto2 = new UserCreateDto(
                "MichaelJohnson",
                "michaeljohnson@example.com",
                "Mega_str0ng_passwd3",
                1L
        );
        var mapper = new UserMapperImpl();
        var userWithPremium = mapper.toUser(createDto1);
        userWithPremium.setId(2L);
        var userWithoutPremium = mapper.toUser(createDto2);
        userWithoutPremium.setId(3L);

        var filter1 = new UserFilterDto(
                null,
                null,
                null,
                null,
                true
        );
        var filter2 = new UserFilterDto(
                "John",
                null,
                null,
                null,
                false
        );

        return Stream.of(
                Arguments.of(filter1,
                        List.of(user, userWithPremium),
                        List.of(mapper.toUserDto(user), mapper.toUserDto(userWithPremium))),
                Arguments.of(filter2,
                        List.of(user, userWithoutPremium, userWithPremium),
                        List.of(mapper.toUserDto(user), mapper.toUserDto(userWithoutPremium)))
        );
    }

    @ParameterizedTest
    @MethodSource("provideParams")
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