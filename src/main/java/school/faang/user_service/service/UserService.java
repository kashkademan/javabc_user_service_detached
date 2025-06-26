package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.event.ProfilePicEvent;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.ProfilePicEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final ProfilePicEventPublisher eventPublisher;

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User with id = " + id + " does not exist"));
    }

    @Transactional
    public UserDto updateUserProfilePicture(Long userId, String newFileId, String newSmallFileId) {
        User user = getUserById(userId);
        UserProfilePic currentProfilePic = user.getUserProfilePic();

        String oldFileId = currentProfilePic != null ? currentProfilePic.getFileId() : null;
        String oldSmallFileId = currentProfilePic != null ? currentProfilePic.getSmallFileId() : null;

        UserProfilePic newProfilePic = currentProfilePic != null ? currentProfilePic : new UserProfilePic();
        newProfilePic.setFileId(newFileId);
        newProfilePic.setSmallFileId(newSmallFileId);
        user.setUserProfilePic(newProfilePic);

        User savedUser = userRepo.save(user);

        publishProfilePictureEvent(userId, newFileId, newSmallFileId, oldFileId, oldSmallFileId);

        return userMapper.toDto(savedUser);
    }

    private void publishProfilePictureEvent(Long userId, String newFileId, String newSmallFileId,
                                            String oldFileId, String oldSmallFileId) {
        ProfilePicEvent event = ProfilePicEvent.builder()
                .userId(userId)
                .newFileId(newFileId)
                .newSmallFileId(newSmallFileId)
                .oldFileId(oldFileId)
                .oldSmallFileId(oldSmallFileId)
                .changedAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(event);
    }
}