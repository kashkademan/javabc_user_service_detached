package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorshipRequestDto {

    private long id;

    private long requesterId;

    private long receiverId;

    private RequestStatus status;

    @Size(max = 4096, message = "Описание запроса не может быть больше 4096 символов!")
    @Pattern(regexp = ".*[a-zA-Zа-яА-ЯёЁ]+.*", message = "Описание запроса не может содержать только цифры!")
    private String description;

    @Size(max = 4096, message = "Описание запроса не может быть больше 4096 символов!")
    private String rejectionReason;
}