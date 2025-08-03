package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;

/**
 * AvatarService — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 03.08.2025
 */
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final UserContext context;

    public void generateUserAvatar() {
    }
}