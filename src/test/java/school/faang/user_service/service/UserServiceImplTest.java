package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserPictureService pictureService;
    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user2 = new User();
        user2.setId(2L);

        userDto1 = new UserDto(1L, "ira", "ira@mail.com", List.of());
        userDto2 = new UserDto(2L, "kira", "kira@mail.com", List.of());
    }

    @Test
    void testGetUsersByIds_whenAllIdsValid_thenReturnAllUserDto() {
        List<Long> ids = List.of(1L, 2L);

        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.getUsersByIds(ids);
        assertEquals(List.of(userDto1, userDto2), result);
    }

    @Test
    void testGetUsersByIds_whenSomeIdsNotFound_thenReturnOnlyTwoUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);

        User user3 = new User();
        user3.setId(3L);
        UserDto userDto3 = new UserDto(3L, "kira", "kira@mail.com", List.of());

        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user3));
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user3)).thenReturn(userDto3);

        List<UserDto> result = userService.getUsersByIds(ids);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(user -> user.getId() == 1L));
        assertTrue(result.stream().anyMatch(user -> user.getId() == 3L));
    }

    @Test
    void testGetUserPersonalWithPicture() {
        long userId = 1L;
        String bigFileId = "FileId";
        String smallFileId = "SmallFileId";

        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(bigFileId);
        profilePic.setSmallFileId(smallFileId);

        User testUser = new User();
        testUser.setId(userId);
        testUser.setUserProfilePic(profilePic);

        UserPersonalDto expectedPersonalDto = new UserPersonalDto();
        expectedPersonalDto.setId(userId);
        expectedPersonalDto.setPictureSmallFileId(smallFileId);
        expectedPersonalDto.setPictureFileId(bigFileId);

        UserPersonalDto intermediatePersonalDto = new UserPersonalDto();
        intermediatePersonalDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertEquals(expectedPersonalDto, userService.getUserPersonals(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserPersonalWithDefaultNotNull() {
        long userId = 1L;

        User testUser = new User();
        testUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(pictureService.getDefaultPictureLink()).thenReturn("notNullValue");

        UserPersonalDto userPersonals = userService.getUserPersonals(userId);
        assertNotNull(userPersonals.getPictureSmallFileId());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testPictureRefresh() {
        long userId = 1L;

        User testUser = new User();
        testUser.setId(userId);

        String notNullNewValue = "notNullNewValue";
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setSmallFileId(notNullNewValue);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(pictureService.generateNewPicture()).thenReturn(profilePic);
        when(userRepository.saveAndFlush(testUser)).thenReturn(testUser);

        UserPersonalDto returnedDto = userService.refreshUserAvatar(userId);
        assertEquals(notNullNewValue, returnedDto.getPictureSmallFileId());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).saveAndFlush(any());
    }
}
