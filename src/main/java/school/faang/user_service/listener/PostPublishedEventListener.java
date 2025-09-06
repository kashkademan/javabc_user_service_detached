package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.post.PostPublishedEvent;
import school.faang.user_service.rating_service.service.leaderboard.LeaderboardService;
import school.faang.user_service.rating_service.service.score.ScoreService;

import java.io.IOException;

/**
 * Класс-слушатель (подписчик) ивентов публикации поста
 *
 * @author Linempy
 * @since 23.08.2025
 */
@Slf4j
@Component
public class PostPublishedEventListener extends AbstractMessageListener implements MessageListener {

    private final ScoreService scoreService;
    private final LeaderboardService leaderboardService;

    public PostPublishedEventListener(ObjectMapper objectMapper,
                                      ScoreService scoreService,
                                      LeaderboardService leaderboardService) {
        super(objectMapper);
        this.scoreService = scoreService;
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            PostPublishedEvent event = objectMapper.readValue(message.getBody(), PostPublishedEvent.class);

            int earnedScore = scoreService.getScore(event);

            leaderboardService.incrementOrCreateUserScore(event.authorId(), earnedScore);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // тут сервис считает сколько баллов нужно дать ивенту

        // тут сохраняем в кэш userId: new_score

        // тут асинхронно сохраняем в бд

    }
}