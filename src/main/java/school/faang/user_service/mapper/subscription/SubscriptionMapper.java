package school.faang.user_service.mapper.subscription;

import lombok.Builder;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionDto;
import school.faang.user_service.entity.User;

@Component
public class SubscriptionMapper {

    public SubscriptionDto userToSubscriptionDto(User user) {
        return new SubscriptionDto(
                user.getId(),
                user.getUsername(),
                user.getEmail());
    }

    @Builder
    public User SubscriptionDtoToUser(SubscriptionDto dto) {
        return User.builder()
                .id(dto.getUserId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .build();
    }
}
