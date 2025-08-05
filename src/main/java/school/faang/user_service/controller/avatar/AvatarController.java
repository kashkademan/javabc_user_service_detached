package school.faang.user_service.controller.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.avatar.AvatarDownloadDto;
import school.faang.user_service.service.avatar.AvatarService;

/**
 * Контроллер для работы с аватарами пользователей.
 * Предоставляет REST API для генерации и загрузки аватаров.
 *
 * @author Linempy
 * @since 03.08.2025
 */
@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService service;

    /**
     * Генерирует новый аватар для текущего пользователя
     *
     * @return HTTP 204 No Content при успешной генерации
     */
    @GetMapping
    public ResponseEntity<Void> generateAvatar() {
        service.generateAndSaveAvatar();
        return ResponseEntity.noContent().build();
    }

    /**
     * Скачивает аватар текущего пользователя
     *
     * @return HTTP 200 OK с данными аватара в теле ответа
     */
    @PostMapping
    public ResponseEntity<AvatarDownloadDto> download() {
        AvatarDownloadDto result = service.downloadAvatar();
        return ResponseEntity.ok().body(result);
    }
}