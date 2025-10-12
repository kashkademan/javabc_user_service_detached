package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.LeaderBoardConfig;
import school.faang.user_service.entity.user.ActionType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserScoreEvent;
import school.faang.user_service.repository.user.UserScoreRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserScoreService {
    private final UserScoreRepository userScoreRepository;
    private final LeaderBoardConfig leaderBoardConfig;
    private final LeaderBoardCacheService leaderBoardCacheService;

    public void addScore(User user, ActionType actionType) {
        int points = leaderBoardConfig.getPointsFor(actionType);

        UserScoreEvent event = new UserScoreEvent();
        event.setUser(user);
        event.setUserId(user.getId());
        event.setActionType(actionType);
        event.setPoints(points);
        event.setUsername(user.getUsername());

        userScoreRepository.save(event);
        leaderBoardCacheService.addScore(user.getUsername(), points);

        log.info("Added {} points for action {} by userId {}", points, actionType, user.getId());
    }

}
