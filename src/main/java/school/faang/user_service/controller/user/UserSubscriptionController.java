package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("followers")
@Tag(name = "Подписки", description = "Операции подписки/отписки между пользователями")
public class UserSubscriptionController {
    private final UserSubscriptionService subscriptionService;
    private final UserContext userContext;

    @PostMapping("/{followeeId}")
    @Operation(summary = "Подписаться на пользователя", description = "Текущий пользователь подписывается на указанного по ID")
    public ResponseEntity<Void> followUser(@PathVariable Long followeeId) {
        Long followerId = userContext.getUserId();
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{followeeId}")
    @Operation(summary = "Отписаться от пользователя", description = "Текущий пользователь отписывается от указанного по ID")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long followeeId) {
        Long followerId = userContext.getUserId();
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{followeeId}/count")
    @Operation(summary = "Получить количество подписчиков", description = "Возвращает количество подписчиков у пользователя по ID")
    public ResponseEntity<CountResponse> getFollowersCount(@PathVariable Long followeeId) {
        return ResponseEntity.ok(subscriptionService.getFollowersCount(followeeId));
    }

    @GetMapping("{followerId}/followees-count")
    @Operation(summary = "Получить количество подписок", description = "Возвращает количество пользователей, на которых подписан данный пользователь")
    public ResponseEntity<CountResponse> getFolloweesCount(@PathVariable Long followerId) {
        return ResponseEntity.ok(subscriptionService.getFolloweesCount(followerId));
    }

    @GetMapping("/{followeeId}/followers")
    @Operation(summary = "Получить список подписчиков", description = "Возвращает список пользователей, подписанных на указанного пользователя")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable Long followeeId) {
        return ResponseEntity.ok(subscriptionService.getFollowers(followeeId));
    }

    @GetMapping("/{followerId}/followees")
    @Operation(summary = "Получить список подписок", description = "Возвращает список пользователей, на которых подписан указанный пользователь")
    public ResponseEntity<List<UserDto>> getFollowees(@PathVariable Long followerId) {
        return ResponseEntity.ok(subscriptionService.getFollowees(followerId));
    }
}
