package school.faang.user_service.service;

import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    User getReferenceById(long userId);

    long findUniqueIdByUsername(String username);

    User findUserById(long userId);

    void checkUserExists(Long userId);

    boolean existsById(long userId);

    UserDto deactivateUser();

    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    UserDto createUser(CreateUserDto createUserDto);

    void followUser(long followerId, long followeeId);
}