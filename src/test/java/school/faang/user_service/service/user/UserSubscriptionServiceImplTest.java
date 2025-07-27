package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.filter.UserNamePatternFilterTest;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.repository.user.UserRepository;


import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * UserSubscriptionServiceImplTest — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 20.07.2025
 */
@ExtendWith(MockitoExtension.class)
public class UserSubscriptionServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserContext userContext;

    private final UserFilter userNameFilter = new UserNamePatternFilterTest();
    private final UserFilter userPhoneFilter = new UserNamePatternFilterTest();
    private final UserFilter userExperienceFilter = new UserNamePatternFilterTest();

    @BeforeEach
    void setUp() {
        userSubscriptionService = new UserSubscriptionServiceImpl(
                subscriptionRepository,
                userRepository,
                mapper,
                userContext,
                List.of(userNameFilter, userPhoneFilter, userExperienceFilter)
        );
    }

    @Mock
    private List<UserFilter> filters;

    @Spy
    private UserMapper mapper;

    @InjectMocks
    private UserSubscriptionServiceImpl userSubscriptionService;

    @Test
    public void testFollowUserFollowsHimself() {
        long followeeId = 1L;

        when(userContext.getUserId()).thenReturn(followeeId);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> userSubscriptionService.followUser(followeeId));

        assertEquals("Нельзя подписаться на самого себя", exception.getMessage());
    }

    @Test
    public void testFollowUserUnfollowHimself() {
        long followeeId = 1L;

        when(userContext.getUserId()).thenReturn(followeeId);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> userSubscriptionService.unfollowUser(followeeId));

        assertEquals("Нельзя отписаться от самого себя", exception.getMessage());
    }

    @Test
    public void testFollowWhenFollowed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId()).thenReturn(followerId);
        when(subscriptionRepository
                .existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> userSubscriptionService.followUser(followeeId));

        assertEquals("Вы уже подписаны на этого пользователя", exception.getMessage());
    }

    @Test
    public void testUnfollowWhenUnfollowed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId()).thenReturn(followerId);
        when(subscriptionRepository
                .existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> userSubscriptionService.unfollowUser(followeeId));

        assertEquals("Вы не подписаны на этого пользователя", exception.getMessage());
    }

    @Test
    public void testFollowUserSuccess() {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId()).thenReturn(followerId);
        when(subscriptionRepository
                .existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);

        userSubscriptionService.followUser(followeeId);

        Mockito.verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserSuccess() {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId()).thenReturn(followerId);
        when(subscriptionRepository
                .existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        userSubscriptionService.unfollowUser(followeeId);

        Mockito.verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowerCount() {
        long followeeId = 1L;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(5);

        CountResponse result = userSubscriptionService.getFollowersCount(followeeId);

        assertEquals(5, result.getCount());
    }

    @Test
    public void testGetFolloweeCount() {
        long followerId = 1L;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(5);

        CountResponse result = userSubscriptionService.getFolloweesCount(followerId);

        assertEquals(5, result.getCount());
    }

    @Test
    public void testGetFollowers() {
        User follower1 = User.builder().id(1L).username("name").experience(3).phone("123456789").build();
        User follower2 = User.builder().id(2L).username("other").experience(5).phone("987654321").build();

        when(subscriptionRepository.findByFolloweeId(follower1.getId())).thenReturn(Stream.of(follower1, follower2));
        List<UserDto> result = userSubscriptionService.getFollowers(follower1.getId(),
                new UserFiltersDto("name", "123456789", 2, 4));

        assertEquals(1, result.size());
    }
}