package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
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
    private UserMapper userMapper;
    @Mock
    private UserContext userContext;
    @Spy
    private FilterService<User, UserFilterDto> filterService = new UserFilterServiceImpl(filters);

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void getById() {
    }

    @Test
    void getUsers() {
    }
}