package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.LeaderBoardConfig;
import school.faang.user_service.dto.user.LeaderScoreDto;
import school.faang.user_service.dto.user.UserPointsDto;
import school.faang.user_service.entity.user.ActionType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserScoreEvent;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserScoreRepository;
import school.faang.user_service.service.redis.LeaderBoardRedisService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserScoreService {
    private final UserScoreRepository userScoreRepository;
    private final UserRepository userRepository;
    private final LeaderBoardConfig leaderBoardConfig;
    private final LeaderBoardRedisService leaderBoardRedisService;

    public void addScore(User user, ActionType actionType) {
        int points = leaderBoardConfig.getPointsFor(actionType);

        UserScoreEvent event = UserScoreEvent.builder()
                .user(user)
                .userId(user.getId())
                .actionType(actionType)
                .points(points)
                .build();

        userScoreRepository.save(event);
        leaderBoardRedisService.addScore(user.getId(), points);

        log.info("Added {} points for action {} by userId {}", points, actionType, user.getId());
    }

    public List<LeaderScoreDto> getLeaderBoard() {
        List<UserPointsDto> userPoints = leaderBoardRedisService.getTopUsers();

        if (userPoints.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = userPoints.stream()
                .map(UserPointsDto::userId)
                .toList();

        List<UserRepository.UserIdUsername> usernamesData = userRepository.findUsernamesByIds(userIds);
        Map<Long, String> usernamesMap = usernamesData.stream()
                .collect(Collectors.toMap(
                        UserRepository.UserIdUsername::getId,
                        UserRepository.UserIdUsername::getUsername
                ));

        return userPoints.stream()
                .map(userPoint -> new LeaderScoreDto(
                        usernamesMap.get(userPoint.userId()),
                        userPoint.points()
                ))
                .toList();
    }

}
