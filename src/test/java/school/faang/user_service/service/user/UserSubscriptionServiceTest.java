package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.ExperienceFilter;
import school.faang.user_service.filters.NameFilter;
import school.faang.user_service.filters.PhoneFilter;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.EventsPublisher;
import school.faang.user_service.publisher.NewFollowerEventPublisher;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserSubscriptionServiceTest {

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;
    private static final long OTHER_USER_ID = 3L;

    private static final int EXPECTED_COUNT = 3;

    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";
    private static final String USERNAME_FILTER_MATCH = "match";
    private static final String USERNAME_FILTER_NON_MATCH = "other";

    private static final int EXP_LOW = 3;
    private static final int EXP_MID = 5;
    private static final int EXP_HIGH = 7;
    private static final int EXP_MIN = 4;
    private static final int EXP_MAX = 6;

    private static final int EXP_MIN_DEFAULT = 0;
    private static final int EXP_MAX_DEFAULT = Integer.MAX_VALUE;

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventsPublisher eventsPublisher;
    @Mock
    private NewFollowerEventPublisher newFollowerEventPublisher;

    @Spy
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private UserSubscriptionService userSubscriptionService;

    @BeforeEach
    public void setUp() {
        List<UserFilter> userFilters = List.of(
                new NameFilter(),
                new ExperienceFilter(),
                new PhoneFilter()
        );

        userSubscriptionService = new UserSubscriptionServiceImpl(
                subscriptionRepository,
                userRepository,
                userMapper,
                userFilters,
                eventsPublisher,
                newFollowerEventPublisher
        );
    }

    // --------------------------------------------------------------
    // followUser
    // --------------------------------------------------------------

    @Test
    @DisplayName("followUser: subscribes successfully when IDs are valid")
    void followUser_ShouldFollowWhenValidIds() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(false);

        User follower = User.builder()
                .id(FOLLOWER_ID)
                .username(USERNAME_1)
                .build();
        when(userRepository.getByIdOrThrow(FOLLOWER_ID)).thenReturn(follower);

        userSubscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionRepository).followUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(eventsPublisher).publishFollow(FOLLOWER_ID, FOLLOWEE_ID);
        verify(newFollowerEventPublisher).publishFollow(FOLLOWER_ID, FOLLOWEE_ID, USERNAME_1);
    }

    @Test
    @DisplayName("followUser: fails when follower and followee are the same user")
    void followUser_ShouldThrowExceptionWhenSameUser() {
        assertThrows(DataValidationException.class,
                () -> userSubscriptionService.followUser(FOLLOWER_ID, FOLLOWER_ID));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("followUser: fails when subscription already exists")
    void followUser_ShouldThrowExceptionWhenAlreadySubscribed() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> userSubscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }


    // --------------------------------------------------------------
    // unfollowUser
    // --------------------------------------------------------------

    @Test
    @DisplayName("unfollowUser: unsubscribes successfully when relationship exists")
    void unfollowUser_ShouldUnfollowWhenValidIds() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);

        userSubscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionRepository).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    @DisplayName("unfollowUser: fails when follower and followee are the same user")
    void unfollowUser_ShouldThrowExceptionWhenSameUser() {
        assertThrows(DataValidationException.class,
                () -> userSubscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWER_ID));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("unfollowUser: fails when user is not subscribed")
    void unfollowUser_ShouldThrowExceptionWhenNotSubscribed() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> userSubscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }


    // --------------------------------------------------------------
    // count endpoints
    // --------------------------------------------------------------

    @Test
    @DisplayName("getFollowersCount: returns number of followers")
    void getFollowersCount_ShouldReturnCorrectCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWEE_ID))
                .thenReturn(EXPECTED_COUNT);

        CountResponse result = userSubscriptionService.getFollowersCount(FOLLOWEE_ID);

        assertEquals(new CountResponse(EXPECTED_COUNT), result);
    }

    @Test
    @DisplayName("getFolloweesCount: returns number of followees")
    void getFolloweesCount_ShouldReturnCorrectCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(FOLLOWER_ID))
                .thenReturn(EXPECTED_COUNT);

        CountResponse result = userSubscriptionService.getFolloweesCount(FOLLOWER_ID);

        assertEquals(new CountResponse(EXPECTED_COUNT), result);
    }


    // --------------------------------------------------------------
    // filter followers
    // --------------------------------------------------------------

    @Test
    @DisplayName("getFollowers: filters by name correctly")
    void getFollowers_ShouldReturnFilteredUsersByName() {
        UserFiltersDto filter = new UserFiltersDto(
                USERNAME_FILTER_MATCH,
                null,
                EXP_MIN_DEFAULT,
                EXP_MAX_DEFAULT
        );

        User userMatch = createUser(FOLLOWEE_ID, USERNAME_FILTER_MATCH, EXP_LOW);
        User userNonMatch = createUser(OTHER_USER_ID, USERNAME_FILTER_NON_MATCH, EXP_HIGH);
        UserDto expectedUser = createUserDto(FOLLOWEE_ID, USERNAME_FILTER_MATCH);

        assertFollowersFiltered(filter, Stream.of(userMatch, userNonMatch), expectedUser);
    }

    @Test
    @DisplayName("getFollowers: filters by experience range correctly")
    void getFollowers_ShouldReturnFilteredUsersByExperience() {
        UserFiltersDto filter = new UserFiltersDto(
                null,
                null,
                EXP_MID,
                EXP_MAX
        );

        User userLow = createUser(FOLLOWEE_ID, USERNAME_1, EXP_LOW);
        User userHigh = createUser(OTHER_USER_ID, USERNAME_2, EXP_MAX);
        UserDto expectedUser = createUserDto(OTHER_USER_ID, USERNAME_2);

        assertFollowersFiltered(filter, Stream.of(userLow, userHigh), expectedUser);
    }


    // --------------------------------------------------------------
    // filter followees
    // --------------------------------------------------------------

    @Test
    @DisplayName("getFollowees: filters by name correctly")
    void getFollowees_ShouldReturnFilteredUsersByName() {
        UserFiltersDto filter = new UserFiltersDto(
                USERNAME_FILTER_MATCH,
                null,
                EXP_MIN_DEFAULT,
                EXP_MAX_DEFAULT
        );

        User userMatch = createUser(FOLLOWEE_ID, USERNAME_FILTER_MATCH, EXP_HIGH);
        User userNonMatch = createUser(OTHER_USER_ID, USERNAME_FILTER_NON_MATCH, EXP_LOW);
        UserDto expectedUser = createUserDto(FOLLOWEE_ID, USERNAME_FILTER_MATCH);

        assertFolloweesFiltered(filter, Stream.of(userMatch, userNonMatch), expectedUser);
    }

    @Test
    @DisplayName("getFollowees: filters by experience range correctly")
    void getFollowees_ShouldReturnFilteredUsersByExperience() {
        UserFiltersDto filter = new UserFiltersDto(
                null,
                null,
                EXP_MIN,
                EXP_MAX
        );

        User userLow = createUser(FOLLOWEE_ID, USERNAME_1, EXP_LOW);
        User userMid = createUser(OTHER_USER_ID, USERNAME_2, EXP_MID);
        UserDto expectedUser = createUserDto(OTHER_USER_ID, USERNAME_2);

        assertFolloweesFiltered(filter, Stream.of(userLow, userMid), expectedUser);
    }


    // --------------------------------------------------------------
    // locale + preference mapping test
    // --------------------------------------------------------------

    @Test
    @DisplayName("getFollowers: maps locale and preference from User to UserDto correctly")
    void getFollowers_ShouldMapLocaleAndPreferenceFromUser() {
        String localeTag = "en-US";

        User user = User.builder()
                .id(FOLLOWEE_ID)
                .username(USERNAME_1)
                .experience(EXP_MID)
                .locale(Locale.forLanguageTag(localeTag))
                .build();

        ContactPreference cp = ContactPreference.builder()
                .user(user)
                .preference(PreferredContact.EMAIL)
                .build();

        user.setContactPreference(cp);

        UserFiltersDto filter = new UserFiltersDto(
                null,
                null,
                EXP_MIN_DEFAULT,
                EXP_MAX_DEFAULT
        );

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(user));

        List<UserDto> result = userSubscriptionService.getFollowers(FOLLOWEE_ID, filter);

        assertEquals(1, result.size());
        UserDto dto = result.get(0);

        assertEquals(localeTag, dto.locale());
        assertEquals("EMAIL", dto.preference());
    }


    // --------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------

    private User createUser(long id, String username, int experience) {
        return User.builder()
                .id(id)
                .username(username)
                .experience(experience)
                .build();
    }

    private UserDto createUserDto(long id, String username) {
        return new UserDto(id, username, null, null, null, null, null);
    }

    private void assertFolloweesFiltered(UserFiltersDto filter,
                                         Stream<User> users,
                                         UserDto expectedUser) {
        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID))
                .thenReturn(users);
        when(userMapper.toUserDto(any(User.class)))
                .thenReturn(expectedUser);

        List<UserDto> result = userSubscriptionService.getFollowees(FOLLOWER_ID, filter);

        verify(subscriptionRepository).findByFollowerId(FOLLOWER_ID);
        assertEquals(List.of(expectedUser), result);
    }

    private void assertFollowersFiltered(UserFiltersDto filter,
                                         Stream<User> users,
                                         UserDto expectedUser) {
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(users);
        when(userMapper.toUserDto(any(User.class)))
                .thenReturn(expectedUser);

        List<UserDto> result = userSubscriptionService.getFollowers(FOLLOWEE_ID, filter);

        verify(subscriptionRepository).findByFolloweeId(FOLLOWEE_ID);
        assertEquals(List.of(expectedUser), result);
    }
}
