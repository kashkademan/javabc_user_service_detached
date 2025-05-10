package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.validation.EndDateValidatable;
import school.faang.user_service.validation.ValidEndDate;

import java.time.LocalDateTime;
import java.util.List;

import static school.faang.user_service.util.LogsConstants.WRONG_USER_ID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidEndDate
public class EventDto implements EndDateValidatable {
    private Long id;

    @NotBlank(message = "Ивент должен иметь название")
    @Size(min = 3, max = 255, message = "Название должно содержать от 3 до 255 символов")
    private String title;

    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;

    @NotNull(message = "Ивент должен иметь дату начала")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime startDate;

    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime endDate;

    @Size(max = 255, message = "Адрес не должен превышать 255 символов")
    private String location;

    private int maxAttendees;

    @NotNull(message = "ID создателя события не может быть пустым")
    @Positive(message = WRONG_USER_ID)
    private Long ownerId;

    private EventType eventType;

    private EventStatus eventStatus;

    private List<Long> relatedSkills;

    @Override
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @Override
    public LocalDateTime getEndDate() {
        return endDate;
    }
}
