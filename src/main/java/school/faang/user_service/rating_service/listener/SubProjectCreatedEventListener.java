package school.faang.user_service.rating_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import school.faang.user_service.rating_service.dto.project.SubProjectCreatedEvent;
import school.faang.user_service.rating_service.service.leaderboard.LeaderboardService;
import school.faang.user_service.rating_service.service.score.ScoreForActionService;

/**
 * Слушатель событий создания подпроекта
 *
 * @author Linempy
 * @since 02.09.2025
 */
@Component
public class SubProjectCreatedEventListener extends AbstractMessageListener<SubProjectCreatedEvent> {

    private final ScoreForActionService scoreService;
    private final LeaderboardService leaderboardService;

    public SubProjectCreatedEventListener(ObjectMapper objectMapper,
                                      ScoreForActionService scoreService,
                                      LeaderboardService leaderboardService) {
        super(objectMapper);
        this.scoreService = scoreService;
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        handleMessage(message,
                SubProjectCreatedEvent.class,
                event -> {
                    Double earnedScore = scoreService.getScore(event);
                    leaderboardService.processUpdateUserScore(event, earnedScore);
                });
    }
}