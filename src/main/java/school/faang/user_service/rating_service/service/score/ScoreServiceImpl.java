package school.faang.user_service.rating_service.service.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.ScorableEvent;
import school.faang.user_service.rating_service.entity.DefaultScore;
import school.faang.user_service.rating_service.repository.DefaultScoreRepository;

/**
 * ScoreServiceImpl — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 05.09.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    private final DefaultScoreRepository defaultScoreRepository;
    private final AssociationEventToAction eventToAction;
    private final UserContext context;

    public int getScore(ScorableEvent event) {
        DefaultScore score = defaultScoreRepository.findByActionType(event.getActionType());

        if (!score.getIsActive()) {
            log.debug("Правило {} является не активным и пропускается", event.getActionType());
            return 0;
        }

        validateFarm(event);
        return calculateFinalScore(event, score);
    }

    private void validateFarm(ScorableEvent event) {
        // TODO: заглушка
        return;
    }

    private int calculateFinalScore(ScorableEvent event, DefaultScore score) {
        // TODO: заглушка
        int basePoints = score.getBasePoints();
        return applyModifiers(event, basePoints);
    }

    private int applyModifiers(ScorableEvent event, int baseScore) {
        // TODO: заглушка
        return baseScore;
    }
}