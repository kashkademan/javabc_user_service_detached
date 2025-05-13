package school.faang.user_service.mapper.subscription;

import lombok.Builder;
import school.faang.user_service.dto.subscription.SubscriptionDto;
import school.faang.user_service.entity.User;

public class SubscriptionMapper {

    public static SubscriptionDto userToSubscriptionDto(User user) {
        return new SubscriptionDto(
                user.getId(),
                user.getUsername(),
                user.getEmail());
    }

    @Builder
    public static User SubscriptionDtoToUser(SubscriptionDto dto) {
        return User.builder()
                .id(dto.getUserId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .build();
    }
}
