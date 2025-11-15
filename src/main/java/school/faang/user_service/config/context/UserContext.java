package school.faang.user_service.config.context;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.User;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private final ThreadLocal<User> userHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public void setUser(User user) {
        userHolder.set(user);
    }

    public User getUser() {
        return userHolder.get();
    }

    @Deprecated
    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is missing. Please make sure 'x-user-id' header is included in the request.");
        }
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
        userHolder.remove();
    }
}
