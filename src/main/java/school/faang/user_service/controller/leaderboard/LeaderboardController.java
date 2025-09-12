package school.faang.user_service.controller.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.rating_service.dto.user.UserScoreViewDto;
import school.faang.user_service.rating_service.service.leaderboard.LeaderboardService;

import java.util.List;

/**
 * REST-контроллер для управления таблицей лидеров
 *
 * @author Linempy
 * @since 08.09.2025
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/top")
    public List<UserScoreViewDto> getTopUsersScore(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {
        return leaderboardService.getTopScores(size, page);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserScoreViewDto> getUserScore(@PathVariable Long userId) {
        UserScoreViewDto userScore = leaderboardService.getUserScore(userId);
        return ResponseEntity.ok(userScore);
    }
}