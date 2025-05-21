package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CareerUpdateDto {
    @NotNull
    private long id;
    private LocalDate from;
    private LocalDate to;
    private String company;
    private String position;
}