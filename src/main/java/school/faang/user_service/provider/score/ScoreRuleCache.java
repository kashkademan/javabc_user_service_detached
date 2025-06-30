package school.faang.user_service.provider.score;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.score.ScoreRuleRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class ScoreRuleCache {

    private final ScoreRuleRepository scoreRuleRepository;

    private final Map<String, Integer> scoreRules = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> scoreRulesByRole = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    public int getScore(String actionType) {
        return scoreRules.computeIfAbsent(actionType, type -> {
            lock.lock();
            try {
                Integer score = scoreRuleRepository.getScore(type);
                return score != null ? score : 0;
            } finally {
                lock.unlock();
            }
        });
    }

    public int getScoreByRole(String actionType, String role) {
        return scoreRulesByRole
            .computeIfAbsent(actionType, typeKey -> new ConcurrentHashMap<>())
            .computeIfAbsent(role.toUpperCase(), roleKey -> {
                lock.lock();
                try {
                    Integer score = scoreRuleRepository.getScoreByRole(actionType, roleKey);
                    return score != null ? score : 0;
                } finally {
                    lock.unlock();
                }
            });
    }
}
