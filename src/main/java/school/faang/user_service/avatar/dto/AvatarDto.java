package school.faang.user_service.avatar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для передачи информации об аватаре.
 * <p>
 * Этот класс используется для передачи URL аватара между слоями приложения.
 * </p>
 *
 * @author agent
 * @since 26.07.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvatarDto {
    private String url;
}