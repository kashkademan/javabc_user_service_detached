package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDto {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull(message = "Дата начала события обязательна")
    private LocalDateTime startDate;
    @NotNull(message = "Дата окончания события обязательна")
    private LocalDateTime endDate;
    private String location;
    private List<Long> relatedSkills;
    @NotNull(message = "Название события обязательно")
    private EventType type;
}
