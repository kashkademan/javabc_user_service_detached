package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserSubscriptionService;
import school.faang.user_service.config.context.UserContext;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
@Tag(name = "User Subscription Controller", description = "API endpoints for managing user subscriptions")
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @PostMapping("/follow/{followeeId}")
    @Operation(summary = "Follow a user by ID", description = "Allows a user to follow another user by their ID.")
    @ApiResponse(responseCode = "200", description = "User successfully followed",
        content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request",
        content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> followUser(@Parameter(description = "ID of the user to follow", required = true)
                                             @PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        try {
            userSubscriptionService.followUser(followerId, followeeId);
            return ResponseEntity.ok("User with ID " + followerId + " now follows user with ID " + followeeId);
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/unfollow/{followeeId}")
    @Operation(summary = "Unfollow a user by ID", description = "Allows a user to unfollow another user by their ID.")
    @ApiResponse(responseCode = "200", description = "User successfully unfollowed",
        content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request",
        content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> unfollowUser(@Parameter(description = "ID of the user to unfollow", required = true)
                                               @PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        try {
            userSubscriptionService.unfollowUser(followerId, followeeId);
            return ResponseEntity.ok("User with ID " + followerId + " no longer follows user with ID " + followeeId);
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/followers/count/{followeeId}")
    @Operation(summary = "Get followers count by followee ID",
        description = "Returns the count of followers for a given followee ID.")
    @ApiResponse(responseCode = "200", description = "Followers count retrieved successfully",
        content = @Content(schema = @Schema(implementation = CountResponse.class)))
    public CountResponse getFollowersCount(@Parameter(description = "ID of the followee", required = true)
                                           @PathVariable long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followees/count/{followerId}")
    @Operation(summary = "Get followees count by follower ID",
        description = "Returns the count of followees for a given follower ID.")
    @ApiResponse(responseCode = "200", description = "Followees count retrieved successfully",
        content = @Content(schema = @Schema(implementation = CountResponse.class)))
    public CountResponse getFolloweesCount(@Parameter(description = "ID of the follower", required = true)
                                           @PathVariable long followerId) {
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/followers/{followeeId}")
    @Operation(summary = "Get followers by followee ID and filters",
        description = "Returns a list of followers for a given followee ID with optional filters.")
    @ApiResponse(responseCode = "200", description = "Followers retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserDto.class)))
    public List<UserDto> getFollowers(@Parameter(description = "ID of the followee", required = true)
                                      @PathVariable long followeeId,
                                      @Parameter(description = "Filters for followers", required = true)
                                      @RequestBody UserFiltersDto userFiltersDto) {
        return userSubscriptionService.getFollowers(followeeId, userFiltersDto);
    }

    @GetMapping("/followees/{followerId}")
    @Operation(summary = "Get followees by follower ID and filters",
        description = "Returns a list of followees for a given follower ID with optional filters.")
    @ApiResponse(responseCode = "200", description = "Followees retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserDto.class)))
    public List<UserDto> getFollowees(@Parameter(description = "ID of the follower", required = true)
                                      @PathVariable long followerId,
                                      @Parameter(description = "Filters for followees", required = true)
                                      @RequestBody UserFiltersDto userFiltersDto) {
        return userSubscriptionService.getFollowees(followerId, userFiltersDto);
    }
}
