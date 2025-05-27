package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
        User foundById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));
        UserPersonalDto userPersonalDto = userMapper.toUserPersonalDto(foundById);

        if (null == userPersonalDto.getPictureSmallFileId() || userPersonalDto.getPictureSmallFileId().isBlank()) {
            userPersonalDto.setPictureSmallFileId(pictureService.getDefaultPictureSeed());
        }

        return userPersonalDto;
    }

    @Override
    @Transactional
    public UserPersonalDto refreshUsersAvatar(Long userId) {
        User foundById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        UserProfilePic newProfilePic = pictureService.generateNewPictureAndReturn();
        foundById.setUserProfilePic(newProfilePic);

        User savedUser = userRepository.saveAndFlush(foundById);

        return userMapper.toUserPersonalDto(savedUser);
    }
}