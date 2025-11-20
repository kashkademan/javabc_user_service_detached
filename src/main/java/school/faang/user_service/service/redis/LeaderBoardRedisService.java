package school.faang.user_service.service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.LeaderBoardConfig;
import school.faang.user_service.dto.user.UserPointsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class LeaderBoardRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final LeaderBoardConfig leaderBoardConfig;

    public LeaderBoardRedisService(
            @Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
            LeaderBoardConfig leaderBoardConfig
    ) {
        this.redisTemplate = redisTemplate;
        this.leaderBoardConfig = leaderBoardConfig;
    }

    public void addScore(Long userId, int points) {
        String key = leaderBoardConfig.getRedisKey();
        redisTemplate.opsForZSet().incrementScore(key, userId.toString(), points);
        trimLeaderboard(key);
    }

    public List<UserPointsDto> getTopUsers() {
        String key = leaderBoardConfig.getRedisKey();
        Set<ZSetOperations.TypedTuple<String>> top =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, leaderBoardConfig.getMaxLeaders() - 1);

        if (top == null || top.isEmpty()) {
            return List.of();
        }

        List<UserPointsDto> result = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : top) {
            Long userId = Long.parseLong(tuple.getValue());
            result.add(new UserPointsDto(userId, tuple.getScore().intValue()));
        }

        return result;
    }

    public boolean exists(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return size != null && size > 0;
    }

    private void trimLeaderboard(String key) {
        Long total = redisTemplate.opsForZSet().zCard(key);
        if (total != null && total > leaderBoardConfig.getMaxLeaders()) {
            redisTemplate.opsForZSet().removeRange(key, 0, total - leaderBoardConfig.getMaxLeaders() - 1);
        }
    }

}
