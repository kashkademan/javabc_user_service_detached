package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.LeaderBoardConfig;
import school.faang.user_service.dto.user.LeaderScoreDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaderBoardCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final LeaderBoardConfig leaderBoardConfig;

    public void addScore(String username, int points) {
        String key = leaderBoardConfig.getRedisKey();
        redisTemplate.opsForZSet().incrementScore(key, username, points);
        trimLeaderboard(key);
    }

    public List<LeaderScoreDto> getTopUsers() {
        String key = leaderBoardConfig.getRedisKey();
        Set<ZSetOperations.TypedTuple<String>> top =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, leaderBoardConfig.getSize() - 1);

        if (top == null) {
            return List.of();
        }

        List<LeaderScoreDto> result = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : top) {
            result.add(new LeaderScoreDto(tuple.getValue(), tuple.getScore().intValue()));
        }

        return result;
    }

    public boolean exists(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return size != null && size > 0;
    }

    public void clearLeaderBoard() {
        String key = leaderBoardConfig.getRedisKey();
        redisTemplate.delete(key);
    }

    private void trimLeaderboard(String key) {
        Long total = redisTemplate.opsForZSet().zCard(key);
        if (total != null && total > leaderBoardConfig.getSize()) {
            redisTemplate.opsForZSet().removeRange(key, 0, total - leaderBoardConfig.getSize() - 1);
        }
    }

}
