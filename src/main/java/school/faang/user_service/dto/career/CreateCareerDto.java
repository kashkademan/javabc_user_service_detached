package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateCareerDto extends BaseCareerDtoWithDates {
    private final String company;
    private final String position;

    public CreateCareerDto(@NotNull LocalDate from,
                           LocalDate to,
                           @NotBlank String company,
                           @NotBlank String position) {
        super(from, to);
        this.company = company;
        this.position = position;
    }
}