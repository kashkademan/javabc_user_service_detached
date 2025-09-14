package school.faang.user_service.rating_service.service.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.rating_service.entity.ScorableEvent;
import school.faang.user_service.rating_service.entity.EventScore;
import school.faang.user_service.rating_service.repository.EventScoreRepository;

/**
 * Сервис для получения кол-ва баллов за выполненное действие пользователем.
 * Реализация интерфейса {@link ScoreForActionService}
 *
 * @author Linempy
 * @since 05.09.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreForActionServiceImpl implements ScoreForActionService {
    private final EventScoreRepository eventScoreRepository;

    public Double getScore(ScorableEvent event) {
        EventScore score = eventScoreRepository.findByActionTypeOrThrows(event.getActionType());

        if (!score.getIsActive()) {
            log.debug("Правило {} является не активным и пропускается", event.getActionType());
            return (double) 0;
        }

        validateFarm(event);
        return calculateFinalScore(event, score);
    }

    private void validateFarm(ScorableEvent event) {
        // TODO: заглушка
    }

    private Double calculateFinalScore(ScorableEvent event, EventScore score) {
        // TODO: заглушка
        Double basePoints = score.getBasePoints();
        return applyModifiers(event, basePoints);
    }

    private Double applyModifiers(ScorableEvent event, Double baseScore) {
        // TODO: заглушка
        return baseScore;
    }
}