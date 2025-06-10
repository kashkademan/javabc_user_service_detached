package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserPictureService;
import school.faang.user_service.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final UserPictureService pictureService;

    @Override
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto existingUser = findUserById(userDto.getId());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setUsername(userDto.getUsername());
        existingUser.setMentors(userDto.getMentors());
        User user = userRepository.save(userMapper.toUser(existingUser));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserPersonalDto getUserPersonals(Long userId) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));
        UserPersonalDto userPersonalDto = userMapper.toUserPersonalDto(foundUser);

        if (StringUtils.isBlank(userPersonalDto.getPictureSmallFileId())) {
            userPersonalDto.setPictureSmallFileId(pictureService.getDefaultPictureLink());
        }

        return userPersonalDto;
    }

    @Override
    @Transactional
    public UserPersonalDto refreshUserAvatar(Long userId) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        if (foundUser.getUserProfilePic() != null && foundUser.getUserProfilePic().getFileId() != null) {
            throw new IllegalStateException("User use photo as avatar");
        }

        UserProfilePic newProfilePic = new UserProfilePic();
        newProfilePic.setSmallFileId(pictureService.generateNewSmallPicture());
        foundUser.setUserProfilePic(newProfilePic);

        User savedUser = userRepository.saveAndFlush(foundUser);

        return userMapper.toUserPersonalDto(savedUser);
    }
}