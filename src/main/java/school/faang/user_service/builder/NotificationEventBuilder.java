package school.faang.user_service.builder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.NotificationEventType;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.User.UserMapper;
import school.faang.user_service.producer.NotificationEventPublisher;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class NotificationEventBuilder {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final NotificationEventPublisher publisher;

    public void createPublish(long userId, NotificationEventType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("ошибка отправки уведомления"));

        UserDto userDto = userMapper.toDto(user);
        userDto.setPreference(user.getContactPreference().getPreference());
        publisher.publishNotification(userDto, type);
    }
}