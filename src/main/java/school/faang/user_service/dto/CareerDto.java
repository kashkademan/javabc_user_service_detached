package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CareerDto {
    private Long id;

    @NotNull
    @PastOrPresent
    private LocalDate dateFrom;

    @PastOrPresent
    private LocalDate dateTo;

    @NotBlank
    private String company;

    @NotBlank
    private String position;
}
