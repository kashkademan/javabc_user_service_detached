package school.faang.user_service.rating_service.service;

import io.lettuce.core.RedisConnectionException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import school.faang.user_service.rating_service.dto.user.UserScoreProjection;
import school.faang.user_service.rating_service.service.leaderboard.postgres.PostgresService;
import school.faang.user_service.rating_service.service.leaderboard.redis.RedisService;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для заполнения ("разогрева") Redis данными из PostgreSQL.
 * Используется в случае, когда Redis хранит данные только in-memory
 *
 * @author Linempy
 * @since 08.09.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WarmUpCacheService {

    private final PostgresService postgresService;
    private final RedisService redisService;

    @Value("${redis.warmup.top-users-limit}")
    private int topUsersLimit;

    @Value("${redis.warmup.top-users-offset}")
    private int offset;

    @PostConstruct
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void warmUpCache() {
        log.info("Начало заполнение Redis данными из Postgres...");

        try {
            Page<UserScoreProjection> topUsersPage = postgresService.getTopScores(topUsersLimit, offset);
            Map<Long, Double> topUsers = topUsersPage.getContent().stream()
                    .collect(Collectors.toMap(
                            UserScoreProjection::getUserId,
                            UserScoreProjection::getScore
                    ));

            redisService.replaceLeaderboard(topUsers);
        } catch (RedisConnectionException e) {
            log.error("Ошибка подключения к Redis: {}", e.getMessage(), e);
        }
    }

}