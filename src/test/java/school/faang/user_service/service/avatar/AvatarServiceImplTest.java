package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.avatar.AvatarDownloadDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.AvatarProcessingException;
import school.faang.user_service.exception.StorageException;
import school.faang.user_service.mapper.AvatarMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.storage.FileStorageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.avatar.AvatarServiceImplData.DEFAULT_CONTENT_TYPE;
import static school.faang.user_service.service.avatar.AvatarServiceImplData.DEFAULT_FILE_DATA;
import static school.faang.user_service.service.avatar.AvatarServiceImplData.USER_ID_1;
import static school.faang.user_service.service.avatar.AvatarServiceImplData.mockUserContext;

/**
 * Тестовый класс для {@link AvatarServiceImpl}.
 *
 * @author Linempy
 * @since 06.08.2025
 */
@DisplayName("Тестирование сервиса работы с аватарами пользователей")
@ExtendWith(MockitoExtension.class)
public class AvatarServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private DiceBearAvatarService diceBearService;

    @Mock
    private FileStorageService storageService;

    @Mock
    private UserRepository repository;

    @Spy
    private AvatarMapper mapper;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(avatarService, "bucketName", "test-bucket");
    }

    @Test
    @DisplayName("generateAndSaveAvatar должен успешно генерировать и сохранять аватар при корректных данных")
    public void testGenerateAndSaveAvatarSuccess() {
        User user = new User();

        mockUserContext(userContext, USER_ID_1);
        when(diceBearService.generateRandomAvatar()).thenReturn(DEFAULT_FILE_DATA);
        when(repository.getByIdOrThrow(USER_ID_1)).thenReturn(user);

        avatarService.generateAndSaveAvatar();

        verify(storageService, times(1))
            .upload(
                eq(DEFAULT_FILE_DATA),
                eq("test-bucket"),
                argThat(key -> key.startsWith("ava/user_1_")),
                eq(MediaType.IMAGE_PNG_VALUE)
            );
        verify(repository).save(user);
        assertNotNull(user.getAvatarKey());
    }

    @Test
    @DisplayName("downloadAvatar должен возвращать DTO аватара при успешном скачивании")
    public void testDownloadAvatarSuccess() {
        String objectKey = "ava/user_1_123.png";
        String expectedFilename = "avatar_user_1.png";
        AvatarDownloadDto expectedDto = new AvatarDownloadDto(
                DEFAULT_FILE_DATA, DEFAULT_CONTENT_TYPE, expectedFilename, DEFAULT_FILE_DATA.length
        );

        doReturn(expectedDto)
            .when(mapper)
            .toDownloadDto(
                    eq(DEFAULT_FILE_DATA),
                    eq(DEFAULT_CONTENT_TYPE),
                    eq(expectedFilename)
            );
        mockUserContext(userContext, USER_ID_1);
        when(repository.getAvatarKeyByIdOrThrow(USER_ID_1)).thenReturn(objectKey);
        when(storageService.download(anyString(), eq(objectKey))).thenReturn(DEFAULT_FILE_DATA);

        AvatarDownloadDto result = avatarService.downloadAvatar();

        assertNotNull(result, "Dto не может быть Null");
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("downloadAvatar должен выбрасывать AvatarProcessingException при ошибке хранилища")
    void testDownloadAvatarShouldThrowError() {
        String objectKey = "ava/user_1_123.png";

        mockUserContext(userContext, USER_ID_1);
        when(repository.getAvatarKeyByIdOrThrow(USER_ID_1)).thenReturn(objectKey);
        when(storageService.download(anyString(), eq(objectKey))).thenThrow(new StorageException("Test error"));

        assertThrows(AvatarProcessingException.class, () -> avatarService.downloadAvatar());
    }
}