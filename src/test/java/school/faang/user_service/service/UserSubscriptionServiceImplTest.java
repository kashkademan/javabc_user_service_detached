package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.service.user.UserSubscriptionServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для проверки подписок")
public class UserSubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private FollowerEventPublisher eventPublisher;

    @InjectMocks
    private UserSubscriptionServiceImpl subscriptionService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Подписка на пользователя - успешный сценарий")
    void shouldFollowUserSuccessfully() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);
        doNothing().when(eventPublisher).publish(any());

        subscriptionService.followUser(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Проверка логики отписки")
    void unfollowUser() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Получение количества подписчиков")
    void testGetFollowersCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(1L))
                .thenReturn(5);

        CountResponse actual = subscriptionService.getFollowersCount(1L);

        assertEquals(5, actual.count());
    }

    @Test
    @DisplayName("Получение количества подписок")
    void testGetFolloweesCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(1L))
                .thenReturn(5);

        CountResponse actual = subscriptionService.getFolloweesCount(1L);
        assertEquals(5, actual.count());
    }

    @Test
    @DisplayName("Получение всех подписчиков")
    void testGetFollowers() {
        long followeeId = 1L;

        User user = new User();

        UserDto userDto = userMapper.toUserDto(user);

        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user));

        List<UserDto> userDtoList = List.of(userDto);

        assertEquals(userDtoList, subscriptionService.getFollowers(followeeId));
        verify(subscriptionRepository).findByFolloweeId(followeeId);
    }

    @Test
    @DisplayName("Получение всех подписок пользователя")
    void testGetFollowees() {
        long followerId = 1L;

        User user = new User();

        UserDto userDto = userMapper.toUserDto(user);

        when(subscriptionRepository.findByFollowerId(followerId))
                .thenReturn(Stream.of(user));

        List<UserDto> userDtoList = List.of(userDto);

        assertEquals(userDtoList, subscriptionService.getFollowees(followerId));
        verify(subscriptionRepository).findByFollowerId(followerId);
    }
}
