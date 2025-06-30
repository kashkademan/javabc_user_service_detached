package school.faang.user_service.service.score;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.user.UserScore;
import school.faang.user_service.repository.user.UserScoreRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserScoreService {

    private final UserService userService;
    private final UserScoreRepository userScoreRepository;

    @Transactional(readOnly = true)
    public List<UserScore> getUserScores() {
        return userScoreRepository.findAll();
    }

    @Transactional
    public int incrementUserScore(long userId, int scoreDelta) {
        userService.getUserByIdOrThrow(userId);
        return userScoreRepository.upsertAndIncrementScore(userId, scoreDelta);
    }
}
