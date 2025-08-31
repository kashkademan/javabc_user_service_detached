package school.faang.user_service.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;

/**
 * RatingServiceImpl — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 29.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final DefaultScoreRepository defaultScoreRepository;
    private final UserContext context;


    public void do(ActionType type) {

    }


    private boolean isDefaultScore(ActionType type) {
        return defaultScoreRepository.
    }



}