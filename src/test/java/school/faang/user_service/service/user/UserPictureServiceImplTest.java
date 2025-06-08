package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.AvatarConfiguration;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;
import school.faang.user_service.util.ImageUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPictureServiceImplTest {
    @Mock
    private AvatarConfiguration config;

    @Mock
    private S3Service s3Service;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserPictureServiceImpl userPictureService;

    @Test
    void uploadAvatarShouldUploadFilesAndUpdateUser() throws IOException {
        long userId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        byte[] dummyBytes = "avatar".getBytes();

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockFile.getOriginalFilename()).thenReturn("avatar.png");
        when(mockFile.getName()).thenReturn("file");
        when(mockFile.getContentType()).thenReturn("image/png");

        when(config.getBigImageLimit()).thenReturn(512);
        when(config.getSmallImageLimit()).thenReturn(64);
        when(config.getImageLimitSize()).thenReturn(1);
        when(config.getBucketSubstorage()).thenReturn("userpic/");

        mockStatic(ImageUtils.class);
        when(ImageUtils.resizeImageToFitLongestSide(eq(mockFile), anyInt()))
                .thenReturn(new ByteArrayInputStream(dummyBytes));

        when(s3Service.uploadFile(any(), any())).thenReturn("userpic/avatarBig", "userpic/avatarSmall");

        when(userRepository.saveAndFlush(any())).thenReturn(user);

        UserPersonalDto result = userPictureService.uploadAvatar(userId, mockFile);

        assertNotNull(result);
        verify(s3Service, times(2)).uploadFile(any(), any());
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void getAvatarShouldReturnAvatarBytesWhenSizeIsBig() throws IOException {
        long userId = 42L;
        byte[] expected = "imageData".getBytes();

        User user = new User();
        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("userpic/avatarBig");
        pic.setSmallFileId("userpic/avatarSmall");
        user.setUserProfilePic(pic);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(config.getBucketSubstorage()).thenReturn("userpic/");
        when(s3Service.downloadFile("userpic/avatarBig")).thenReturn(new ByteArrayInputStream(expected));

        byte[] result = userPictureService.getAvatar(userId, "b");

        assertArrayEquals(expected, result);
        verify(s3Service).downloadFile("userpic/avatarBig");
    }
    @Test

    void getAvatarShouldThrowExceptionWhenInvalidSizeMarker() {
        assertThrows(IllegalArgumentException.class, () -> userPictureService.getAvatar(1L, "large"));
    }

    @Test
    void getAvatarShouldThrowExceptionWhenNoAvatarPresent() {
        long userId = 5L;
        User user = new User();
        user.setUserProfilePic(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> userPictureService.getAvatar(userId, "b"));
    }

    @Test
    void deleteAvatarShouldRemoveFilesAndClearProfilePic() {
        long userId = 100L;
        User user = new User();

        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("userpic/avatarBig");
        pic.setSmallFileId("userpic/avatarSmall");
        user.setUserProfilePic(pic);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(config.getRandomPictureProviderRootUrl()).thenReturn("https://avatars.example.com");
        when(userRepository.saveAndFlush(user)).thenReturn(user);

        userPictureService.deleteAvatar(userId);

        verify(s3Service).deleteFile("userpic/avatarBig");
        verify(s3Service).deleteFile("userpic/avatarSmall");
        assertNull(user.getUserProfilePic());
        verify(userRepository).saveAndFlush(user);
    }
}