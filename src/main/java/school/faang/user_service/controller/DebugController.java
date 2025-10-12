package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.user.LeaderBoardCacheService;

@RequiredArgsConstructor
@RequestMapping("/debug")
@RestController
public class DebugController {

    private final LeaderBoardCacheService leaderBoardCacheService;

    @DeleteMapping("/leader-board/clear-cache")
    public ResponseEntity<String> clearLeaderBoardCache() {
        leaderBoardCacheService.clearLeaderBoard();
        return ResponseEntity.ok("Leaderboard cache cleared successfully.");
    }
}
