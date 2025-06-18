package school.faang.user_service.dto.contact;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactPreferenceRequestDto {
    @NotNull(message = "Preference is mandatory")
    private PreferredContact preference;
    @NotNull(message = "User id is mandatory")
    private long userId;
}
