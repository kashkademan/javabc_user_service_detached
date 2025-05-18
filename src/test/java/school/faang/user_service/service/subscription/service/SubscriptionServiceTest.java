package school.faang.user_service.service.subscription.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.subscription.UserFilterStrategy;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.subscription.SubscriptionServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    List<UserFilterStrategy> strategies;
    @Mock
    UserFilterStrategy userFilterStrategyForExpMin;
    @Mock
    UserFilterStrategy userFilterStrategyForName;
    @Mock
    UserFilterStrategy userFilterStrategyForPhoneNumber;
    @Mock
    UserFilterStrategy userFilterStrategyForExpMax;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;

    @Test
    void testUserFollowedSuccess() {
        bothExist();

        subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionRepository).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testUserFollowedFailureWithException() {
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
        when(userRepository.existsById(FOLLOWEE_ID)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    void testUserFollowedFailureSubToOneSelf() {
        followerExists();
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(FOLLOWER_ID, FOLLOWER_ID));
    }

    @Test
    void testUnfollowSuccess() {
        bothExist();
        subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testUnfollowFailureWithException() {
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(FOLLOWER_ID, FOLLOWER_ID));
    }

    @Test
    void testGetFollowersFailureWithMinExp() {
        followeeExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("XX", "947", 3, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWER_ID);
        matchingUser.setAboutMe("NN");
        matchingUser.setPhone("444");
        matchingUser.setExperience(2);

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForExpMin
        ));

        when(userFilterStrategyForExpMin.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForExpMin.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MinExpTested " + (user.getExperience()>filter.getExperienceMin()));
            return user.getExperience()>filter.getExperienceMin();
        });
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowers(FOLLOWEE_ID, userDtoFilter));
    }

    @Test
    void testGetFollowersFailureWithMaxExp() {
        followeeExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("XX", "947", 3, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWER_ID);
        matchingUser.setAboutMe("NN");
        matchingUser.setPhone("444");
        matchingUser.setExperience(22);

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForExpMax
        ));

        when(userFilterStrategyForExpMax.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForExpMax.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MaxExpTested " + (user.getExperience()<filter.getExperienceMax()));
            return user.getExperience()<filter.getExperienceMax();
        });
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowers(FOLLOWEE_ID, userDtoFilter));
    }


    @Test
    void testGetFollowersFailureWithNamePattern() {
        followeeExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("XX", "947", 3, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWER_ID);
        matchingUser.setAboutMe("NN");
        matchingUser.setPhone("444");
        matchingUser.setExperience(22);

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForName
        ));

        when(userFilterStrategyForName.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForName.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("Name Tested " + user.getAboutMe().contains(filter.getNamePattern()));
            return user.getAboutMe().contains(filter.getNamePattern());
        });
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowers(FOLLOWEE_ID, userDtoFilter));
    }


    @Test
    void testGetFollowersFailureWithPhoneNumberPattern() {
        followeeExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("XX", "947", 3, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWER_ID);
        matchingUser.setAboutMe("NN");
        matchingUser.setPhone("444");
        matchingUser.setExperience(22);

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForPhoneNumber
        ));

        when(userFilterStrategyForPhoneNumber.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForPhoneNumber.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("Phone Tested " + user.getAboutMe().contains(filter.getNamePattern()));
            return user.getPhone().equals(filter.getPhonePattern());
        });
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowers(FOLLOWEE_ID, userDtoFilter));
    }



    @Test
    void testGetFollowersSuccess() {
        followeeExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("NN", "947", 1, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWER_ID);
        matchingUser.setAboutMe("NN");
        matchingUser.setPhone("947");
        matchingUser.setExperience(5);

        UserDto userDto = new UserDto(FOLLOWER_ID, "username", "username@mail.ru",
                new ArrayList<>(), "NN", 5, "947");

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForExpMin,
                userFilterStrategyForExpMax,
                userFilterStrategyForName,
                userFilterStrategyForPhoneNumber
        ));

        when(userFilterStrategyForExpMin.isApplicable(any())).thenReturn(true);
        when(userFilterStrategyForExpMax.isApplicable(any())).thenReturn(true);
        when(userFilterStrategyForName.isApplicable(any())).thenReturn(true);
        when(userFilterStrategyForPhoneNumber.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForExpMin.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MinExpTested " + (user.getExperience()>filter.getExperienceMin()));
            return user.getExperience()>filter.getExperienceMin();
        });

        when(userFilterStrategyForExpMax.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MaxExpTested " + (user.getExperience()<filter.getExperienceMax()));
            return user.getExperience()<filter.getExperienceMax();
        });

        when(userFilterStrategyForName.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("Name Tested " + user.getAboutMe().contains(filter.getNamePattern()));
            return user.getAboutMe().contains(filter.getNamePattern());
        });

        when(userFilterStrategyForPhoneNumber.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
          User user = invocation.getArgument(0);
          UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("Phone Tested " + user.getAboutMe().contains(filter.getNamePattern()));
            return user.getPhone().equals(filter.getPhonePattern());
        });

        when(userMapper.mapListOfUsers(List.of(matchingUser))).thenReturn(List.of(userDto));

        List<UserDto> result = subscriptionService.getFollowers(FOLLOWEE_ID, userDtoFilter);
        System.out.println(result);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(FOLLOWER_ID, result.get(0).getId());
        Assertions.assertEquals("NN", result.get(0).getAboutMe());
        Assertions.assertEquals("947", result.get(0).getPhone());
    }

    @Test
    public void getFollowerCountSuccess() {
        followeeExists();
        when(subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWEE_ID)).thenReturn(3);
        int count = subscriptionService.getFollowerCount(FOLLOWEE_ID);
        Assertions.assertEquals(3, count);
    }




    @Test
    public void testGetFollowingSuccess() {
        followerExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("NN", "947", 1, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWEE_ID);
        matchingUser.setAboutMe("NN");
        matchingUser.setPhone("947");
        matchingUser.setExperience(5);

        UserDto userDto = new UserDto(FOLLOWEE_ID, "username", "username@mail.ru",
                new ArrayList<>(), "NN", 5, "947");

        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForExpMin,
                userFilterStrategyForExpMax,
                userFilterStrategyForName,
                userFilterStrategyForPhoneNumber
        ));

        when(userFilterStrategyForExpMin.isApplicable(any())).thenReturn(true);
        when(userFilterStrategyForExpMax.isApplicable(any())).thenReturn(true);
        when(userFilterStrategyForName.isApplicable(any())).thenReturn(true);
        when(userFilterStrategyForPhoneNumber.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForExpMin.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MinExpTested " + (user.getExperience()>filter.getExperienceMin()));
            return user.getExperience()>filter.getExperienceMin();
        });

        when(userFilterStrategyForExpMax.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MaxExpTested " + (user.getExperience()<filter.getExperienceMax()));
            return user.getExperience()<filter.getExperienceMax();
        });

        when(userFilterStrategyForName.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("Name Tested " + user.getAboutMe().contains(filter.getNamePattern()));
            return user.getAboutMe().contains(filter.getNamePattern());
        });

        when(userFilterStrategyForPhoneNumber.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("Phone Tested " + user.getAboutMe().contains(filter.getNamePattern()));
            return user.getPhone().equals(filter.getPhonePattern());
        });

        when(userMapper.mapListOfUsers(List.of(matchingUser))).thenReturn(List.of(userDto));

        List<UserDto> result = subscriptionService.getFollowing(FOLLOWER_ID, userDtoFilter);
        System.out.println(result);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(FOLLOWEE_ID, result.get(0).getId());
        Assertions.assertEquals("NN", result.get(0).getAboutMe());
        Assertions.assertEquals("947", result.get(0).getPhone());
    }

    @Test
    void testGetFollowingFailureWithMinExp() {
        followerExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("NN", "947", 6, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWEE_ID);
        matchingUser.setAboutMe("NN");
        matchingUser.setPhone("947");
        matchingUser.setExperience(5);

        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForExpMin
        ));

        when(userFilterStrategyForExpMin.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForExpMin.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MinExpTested " + (user.getExperience()>filter.getExperienceMin()));
            return user.getExperience()>filter.getExperienceMin();
        });
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowing(FOLLOWER_ID, userDtoFilter));
    }

    @Test
    void testGetFollowingFailureWithMaxExp() {
        followerExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("NN", "947", 6, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWEE_ID);
        matchingUser.setAboutMe("NN");
        matchingUser.setPhone("947");
        matchingUser.setExperience(22);

        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForExpMax
        ));

        when(userFilterStrategyForExpMax.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForExpMax.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MaxExpTested " + (user.getExperience()<filter.getExperienceMax()));
            return user.getExperience()<filter.getExperienceMax();
        });
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowing(FOLLOWER_ID, userDtoFilter));
    }


    @Test
    void testGetFollowingFailureWithNamePattern() {
        followerExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("NN", "947", 6, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWEE_ID);
        matchingUser.setAboutMe("XX");
        matchingUser.setPhone("947");
        matchingUser.setExperience(22);

        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForName
        ));

        when(userFilterStrategyForName.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForName.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MaxExpTested " + (user.getAboutMe().contains(filter.getNamePattern())));
            return user.getAboutMe().contains(filter.getNamePattern());
        });
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowing(FOLLOWER_ID, userDtoFilter));
    }


    @Test
    void testGetFollowingFailureWithPhoneNumberPattern() {
        followerExists();

        UserDtoFilter userDtoFilter = new UserDtoFilter("NN", "947", 6, 10);

        User matchingUser = new User();
        matchingUser.setId(FOLLOWEE_ID);
        matchingUser.setAboutMe("XX");
        matchingUser.setPhone("222");
        matchingUser.setExperience(22);

        when(subscriptionRepository.findByFollowerId(FOLLOWER_ID))
                .thenReturn(Stream.of(matchingUser));

        when(strategies.stream()).thenReturn(Stream.of(
                userFilterStrategyForPhoneNumber
        ));

        when(userFilterStrategyForPhoneNumber.isApplicable(any())).thenReturn(true);

        when(userFilterStrategyForPhoneNumber.filterUsers(any(User.class), any(UserDtoFilter.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDtoFilter filter = invocation.getArgument(1);
            System.out.println("MaxExpTested " + (user.getAboutMe().contains(filter.getNamePattern())));
            return user.getPhone().equals(filter.getNamePattern());
        });
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.getFollowing(FOLLOWER_ID, userDtoFilter));
    }

    @Test
    public void testGetFollowingCount(){
        followerExists();
        when(subscriptionRepository.findFolloweesAmountByFollowerId(FOLLOWER_ID)).thenReturn(5);
        int count = subscriptionService.getFollowingCount(FOLLOWER_ID);
        Assertions.assertEquals(5, count);
    }

    private void bothExist() {
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
        when(userRepository.existsById(FOLLOWEE_ID)).thenReturn(true);
    }

    private void followerExists() {
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
    }

    private void followeeExists() {
        when(userRepository.existsById(FOLLOWEE_ID)).thenReturn(true);
    }
}



