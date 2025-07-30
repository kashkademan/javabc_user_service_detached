package school.faang.user_service.avatar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.avatar.dto.AvatarDto;
import school.faang.user_service.avatar.service.UserAvatarService;

/**
 * Контроллер для управления аватарами пользователей.
 * <p>
 * Этот класс предоставляет API для генерации и загрузки аватаров пользователей.
 * Он взаимодействует с сервисом {@link UserAvatarService} для выполнения операций, связанных с аватарами.
 * </p>
 *
 * @author agent
 * @since 26.07.2025
 */
@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final UserAvatarService avatarService;

    @PostMapping("/generate")
    public ResponseEntity<AvatarDto> generate(@RequestParam String username) {
        return ResponseEntity.ok(avatarService.generateAndUpload(username));
    }
}