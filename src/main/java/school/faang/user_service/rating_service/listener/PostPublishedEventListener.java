package school.faang.user_service.rating_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import school.faang.user_service.rating_service.dto.post.PostPublishedEvent;
import school.faang.user_service.rating_service.service.leaderboard.LeaderboardService;
import school.faang.user_service.rating_service.service.score.ScoreForActionService;

/**
 * Класс-слушатель (подписчик) ивентов публикации поста
 *
 * @author Linempy
 * @since 23.08.2025
 */
@Slf4j
@Component
public class PostPublishedEventListener extends AbstractMessageListener<PostPublishedEvent> {

    private final ScoreForActionService scoreService;
    private final LeaderboardService leaderboardService;

    public PostPublishedEventListener(ObjectMapper objectMapper,
                                      ScoreForActionService scoreService,
                                      LeaderboardService leaderboardService) {
        super(objectMapper);
        this.scoreService = scoreService;
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        handleMessage(message,
                PostPublishedEvent.class,
                event -> {
                    Double earnedScore = scoreService.getScore(event);
                    leaderboardService.processUpdateUserScore(event, earnedScore);
                });
    }
}