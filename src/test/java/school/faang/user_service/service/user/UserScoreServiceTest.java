package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserScore;
import school.faang.user_service.repository.score.UserScoreRepository;
import school.faang.user_service.service.score.UserScoreService;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class UserScoreServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserScoreRepository userScoreRepository;

    @InjectMocks
    private UserScoreService userScoreService;

    private Long userId;
    private UserScore userScore;
    private Map<String, Integer> scoreRules;
    private Map<String, Map<String, Integer>> scoreRulesByRole;

    @BeforeEach
    public void setUp() {
        userId = 1L;

        scoreRules = Map.of(
            ScoreActionType.COMPLETE_GOAL.name(), 10
        );

        scoreRulesByRole = Map.of(
            ScoreActionType.COMPLETE_GOAL.name(), Map.of(
                "OWNER", 5,
                "ATTENDEE", 2
            )
        );

        userScore = new UserScore();
        userScore.setUserId(userId);
        userScore.setScore(10);
    }

    @Test
    public void testIncrementUserScore_successfully() {
        int initialScore = userScore.getScore();
        int delta = 10;
        int expectedScore = initialScore + delta;

        User mockUser = Mockito.mock(User.class);
        Mockito.when(userService.getUserByIdOrThrow(userId)).thenReturn(mockUser);
        Mockito.when(userScoreRepository.upsertAndIncrementScore(userId, delta))
                .thenReturn(userScore.getScore() + delta);

        int result = userScoreService.incrementUserScore(userId, delta);

        Assertions.assertEquals(expectedScore, result);

        Mockito.verify(userService).getUserByIdOrThrow(userId);
        Mockito.verify(userScoreRepository).upsertAndIncrementScore(userId, delta);
    }
}
