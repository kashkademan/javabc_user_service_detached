package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
@Tag(name = "Subscription API", description = "API для управления подписками")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{followeeId}")
    @Operation(summary = "Подписаться на пользователя",
            description = "Добавляет подписку на пользователя для пользователя с переданным идентификатором")
    public void followUser(@Parameter(description = "Идентификатор подписываемого") @RequestParam long followerId,
                           @Parameter(description = "Идентификатор последователя") @PathVariable long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followeeId}")
    @Operation(summary = "Отписаться от пользователя",
            description = "Отменяет подписку на пользователя для пользователя с переданным идентификатором")
    public void unfollowUser(@Parameter(description = "Идентификатор подписываемого") @RequestParam long followerId,
                             @Parameter(description = "Идентификатор последователя") @PathVariable long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("/followers-{followeeId}")
    @Operation(summary = "Вывести подписчиков",
            description = "Находит всех подписчиков пользователя с переданным идентификатором" +
                    " и выводит их на основе переданного фильтра")
    public List<UserDto> getFollowers(@Parameter(description = "Идентификатор пользователя")
                                          @PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/followers-{followeeId}")
    @Operation(summary = "Вывести количество подписчиков",
            description = "Считает и выводит количество подписчиков пользователя с переданным идентификатором")
    public int getFollowersCount(@Parameter(description = "Идентификатор пользователя") @PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @PostMapping("/following-{followeeId}")
    @Operation(summary = "Вывести подписки",
            description = "Находит все подписки пользователя с переданным идентификатором" +
            " и выводит их на основе переданного фильтра")
    public List<UserDto> getFollowing(@Parameter(description = "Идентификатор пользователя")
                                          @PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/following-{followerId}")
    @Operation(summary = "Вывести количество подписок",
            description = "Считает и выводит количество подписок пользователя с переданным идентификатором")
    public int getFollowingCount(@Parameter(description = "Идентификатор пользователя") @PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    @GetMapping("/followers-id/{followeeId}")
    @Operation(summary = "Получить идентификаторы подписчиков",
            description = "Возвращает список идентификаторов подписчиков пользователя с переданным идентификатором")
    public List<Long> getFollowerIds(@Parameter(description = "Идентификатор пользователя")
                                         @PathVariable long followeeId) {
        return subscriptionService.getFollowerIds(followeeId);
    }
}
