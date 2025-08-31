package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationDto {

    @NotBlank
    private PreferredContact preferredContact;

    private String email;

    private String phone;

    private Long chatId;
}