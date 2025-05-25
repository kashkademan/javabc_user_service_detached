package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.event.FollowEventDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<FollowEventDto> followUser(@RequestBody FollowEventDto event) {
        subscriptionService.followUser(event.followerId(), event.followeeId());
        return ResponseEntity.status(HttpStatus.OK).body(event);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long id, UserFilterDto filter) {
        return subscriptionService.getFollowers(id, filter);
    }

    public List<UserDto> getFollowing(long id, UserFilterDto filter) {
        return subscriptionService.getFollowing(id, filter);
    }

    public long getFollowersCount(long id) {
        return subscriptionService.getFollowersCount(id);
    }

    public long getFollowingCount(long id) {
        return subscriptionService.getFollowingCount(id);
    }
}