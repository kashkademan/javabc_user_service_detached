package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDto {
    @NotNull
    private Long id;
    @NotBlank(message = "Название события обязательно")
    @Size(max = 64, message = "Превышено максимальное число символов: 64")
    private String title;
    @NotBlank(message = "Описание события обязательно")
    @Size(max = 4096, message = "Превышено максимальное число символов: 4096")
    private String description;
    @NotNull(message = "Дата начала события обязательна")
    private LocalDateTime startDate;
    @NotNull(message = "Дата окончания события обязательна")
    private LocalDateTime endDate;
    @Size(max = 128, message = "Превышено максимальное число символов: 128")
    private String location;
    private List<Long> relatedSkills;
    @NotNull(message = "Тип события обязателен")
    private EventType type;
    @NotNull(message = "Статус события обязателен")
    private EventStatus status;
}
