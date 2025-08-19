package school.faang.user_service.service.avatar;

import school.faang.user_service.config.context.UserContext;

import static org.mockito.Mockito.when;

/**
 * Вспомогательный класс для тестирования {@link AvatarServiceImpl}.
 *
 * @author Linempy
 * @since 06.08.2025
 */
public class AvatarServiceImplData {
    static final Long USER_ID_1 = 1L;
    static final byte[] DEFAULT_FILE_DATA = {1, 2, 3};
    static final String DEFAULT_CONTENT_TYPE = "image/png";

    public static void mockUserContext(UserContext context, Long userId) {
        when(context.getUserId()).thenReturn(userId);
    }
}