package school.faang.user_service.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactPreferenceResponseDto {
    private long id;
    private PreferredContact preference;
}
