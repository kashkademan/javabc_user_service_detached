package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.LeaderScoreDto;
import school.faang.user_service.service.user.LeaderBoardCacheService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/leader-board")
public class LeaderBoardController {

    private final LeaderBoardCacheService leaderboardCacheService;

    @GetMapping("/get-top")
    public List<LeaderScoreDto> getTop() {
        return leaderboardCacheService.getTopUsers();
    }
}
