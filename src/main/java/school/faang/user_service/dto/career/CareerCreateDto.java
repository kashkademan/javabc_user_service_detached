package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CareerCreateDto {
    @NotNull
    private LocalDate from;
    private LocalDate to;
    @NotBlank
    private String company;
    @NotBlank
    private String position;
}