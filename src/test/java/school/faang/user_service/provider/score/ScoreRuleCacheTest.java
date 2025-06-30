package school.faang.user_service.provider.score;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.score.ScoreRuleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ScoreRuleCacheTest {

    @Mock
    private ScoreRuleRepository scoreRuleRepository;

    @InjectMocks
    private ScoreRuleCache scoreRuleCache;

    private final String actionType = "COMPLETE_GOAL";
    private final String role = "owner";

    @Test
    void getScore_shouldReturnScoreAndCache() {
        Mockito.when(scoreRuleRepository.getScore(actionType)).thenReturn(10);

        int firstCall = scoreRuleCache.getScore(actionType);
        int secondCall = scoreRuleCache.getScore(actionType);

        assertEquals(10, firstCall);
        assertEquals(10, secondCall);

        Mockito.verify(scoreRuleRepository, Mockito.times(1)).getScore(actionType);
    }

    @Test
    void getScore_shouldReturnZero() {
        Mockito.when(scoreRuleRepository.getScore(actionType)).thenReturn(null);

        int result = scoreRuleCache.getScore(actionType);

        assertEquals(0, result);
        Mockito.verify(scoreRuleRepository).getScore(actionType);
    }

    @Test
    void getScoreByRole_shouldReturnScore() {
        Mockito.when(scoreRuleRepository.getScoreByRole(actionType, "OWNER")).thenReturn(5);

        int first = scoreRuleCache.getScoreByRole(actionType, role);
        int second = scoreRuleCache.getScoreByRole(actionType, role);

        assertEquals(5, first);
        assertEquals(5, second);

        Mockito.verify(scoreRuleRepository, Mockito.times(1)).getScoreByRole(actionType, "OWNER");
    }

    @Test
    void getScoreByRole_shouldReturnZero() {
        Mockito.when(scoreRuleRepository.getScoreByRole(actionType, "OWNER")).thenReturn(null);

        int result = scoreRuleCache.getScoreByRole(actionType, role);

        assertEquals(0, result);
        Mockito.verify(scoreRuleRepository).getScoreByRole(actionType, "OWNER");
    }
}
