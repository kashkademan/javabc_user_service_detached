package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateCareerDto extends BaseCareerDtoWithDates {
    @NotBlank
    private final String company;
    @NotBlank
    private final String position;

    public UpdateCareerDto(@NotNull LocalDate from, LocalDate to, String company, String position) {
        super(from, to);
        this.company = company;
        this.position = position;
    }
}