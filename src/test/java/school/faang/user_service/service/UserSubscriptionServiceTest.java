package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserExperienceFilterTest;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.filter.UserNameFilterTest;
import school.faang.user_service.filter.UserPhoneFilterTest;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.user.UserSubscriptionService;
import school.faang.user_service.service.user.UserSubscriptionServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    UserFilter userFilterExperience = new UserExperienceFilterTest();
    UserFilter userFilterName = new UserNameFilterTest();
    UserFilter userPhoneName = new UserPhoneFilterTest();

    @Spy
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final User follower = User.builder().id(1L).experience(2).phone("88888").username("follower").build();
    private final User followee = User.builder().id(2L).experience(3).phone("99999").username("followee").build();

    private UserSubscriptionService userSubscriptionService;

    @BeforeEach
    void setUp() {
        userSubscriptionService = new UserSubscriptionServiceImpl(
            subscriptionRepository,
            userRepository,
            userMapper,
            List.of(userFilterExperience, userFilterName, userPhoneName)
        );
    }

    @Test
    void testFollowUser_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).thenReturn(false);

        userSubscriptionService.followUser(follower.getId(), followee.getId());

        verify(subscriptionRepository).followUser(follower.getId(), followee.getId());
    }

    @Test
    void testFollowUser_SelfSubscription() {
        assertThrows(DataValidationException.class,
            () -> userSubscriptionService.followUser(follower.getId(), follower.getId()));
    }

    @Test
    void testFollowUser_AlreadyFollowing() {
        when(userRepository.existsById(follower.getId())).thenReturn(true);
        when(userRepository.existsById(followee.getId())).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(follower.getId(),
            followee.getId())).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> userSubscriptionService.followUser(follower.getId(), followee.getId()));
        assertEquals("You are already following this user.", exception.getMessage());
    }

    @Test
    void testUnfollowUser_Success() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(follower.getId(), followee.getId()))
            .thenReturn(true);

        userSubscriptionService.unfollowUser(follower.getId(), followee.getId());

        verify(subscriptionRepository).unfollowUser(follower.getId(), followee.getId());
    }

    @Test
    void testUnfollowUser_NotFollowing() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> userSubscriptionService.unfollowUser(follower.getId(), followee.getId()));
        assertEquals("User with ID 1 is not following user with ID 2", exception.getMessage());
    }

    @Test
    void testGetFollowersCount_Success() {
        int count = 5;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(anyLong())).thenReturn(count);

        CountResponse response = userSubscriptionService.getFollowersCount(followee.getId());

        assertEquals(count, response.getCount());
    }

    @Test
    void testGetFolloweesCount_Success() {
        int count = 3;
        when(subscriptionRepository.findFolloweesAmountByFollowerId(anyLong())).thenReturn(count);

        CountResponse response = userSubscriptionService.getFolloweesCount(follower.getId());

        assertEquals(count, response.getCount());
    }

    @Test
    void testGetFollowers_Success() {
        User follower1 = User.builder().id(1L).username("follower").experience(3).phone("88888").build();
        User follower2 = User.builder().id(2L).username("follower").experience(2).phone("99999").build();
        Stream<User> followersStream = Stream.of(follower1, follower2);

        when(subscriptionRepository.findByFolloweeId(anyLong())).thenReturn(followersStream);

        List<UserDto> result = userSubscriptionService.getFollowers(
                1L,
                new UserFiltersDto(
                        follower.getUsername(),
                        follower.getPhone(),
                        1,
                        5)
        );
        assertEquals(1, result.size());
        UserDto expectedUserDto = userMapper.toUserDto(follower1);
        UserDto actualUserDto = result.get(0);

        assertEquals(expectedUserDto.id(), actualUserDto.id());
        assertEquals(expectedUserDto.username(), actualUserDto.username());
        assertEquals(expectedUserDto.phone(), actualUserDto.phone());
    }

    @Test
    void testGetFollowers_NoMatch() {
        User follower1 = User.builder().id(1L).username("follower").experience(8).build();
        User follower2 = User.builder().id(2L).username("follower").experience(7).build();
        Stream<User> followersStream = Stream.of(follower1, follower2);

        when(subscriptionRepository.findByFolloweeId(anyLong())).thenReturn(followersStream);

        List<UserDto> result = userSubscriptionService.getFollowers(
                1L,
                new UserFiltersDto(
                        follower.getUsername(),
                        follower.getPhone(),
                        1,
                        5)
        );
        assertTrue(result.isEmpty());
    }
}
