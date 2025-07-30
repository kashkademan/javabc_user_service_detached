package school.faang.user_service.rating_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.rating_service.config.LeaderDto;
import school.faang.user_service.rating_service.rating_aspect.UserIdUsernameProjection;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST-контроллер для получения лидеров по рейтингу пользователей.
 * <p>
 * Работает с Redis, используя Sorted Set с ключом "leaderboard", где:
 * <ul>
 *     <li>элементом является userId в виде строки</li>
 *     <li>score — рейтинг пользователя</li>
 * </ul>
 * Возвращает список пользователей с наивысшими рейтингами.
 */
@RestController
@RequestMapping("/leaders")
@Tag(name = "Таблица лидеров", description = "Получение топа пользователей по баллам")
@RequiredArgsConstructor
public class LeaderBoardController {

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    @GetMapping("/top")
    @Operation(summary = "Получить топ лидеров", description = "Возвращает список пользователей с максимальными баллами из таблицы лидеров")
    public List<LeaderDto> getTopLeaders(
            @RequestParam(defaultValue = "10") int limit) {
        Set<ZSetOperations.TypedTuple<String>> topUsers =
                redisTemplate.opsForZSet().reverseRangeWithScores("leaderboard", 0, limit - 1);

        if (topUsers == null || topUsers.isEmpty()) {
            return Collections.emptyList();
        }

        var userIdToScore = topUsers.stream()
                .collect(Collectors.toMap(
                        tuple -> Long.parseLong(tuple.getValue()),
                        tuple -> tuple.getScore().longValue()
                ));

        List<Long> userIds = userIdToScore.keySet().stream().toList();

        List<UserIdUsernameProjection> users = userRepository.findByIdIn(userIds);

        return users.stream()
                .map(u -> new LeaderDto(
                        u.getId(),
                        u.getUsername(),
                        userIdToScore.getOrDefault(u.getId(), 0L)
                ))
                .sorted((a, b) -> Long.compare(b.getScore(), a.getScore()))
                .toList();
    }
}