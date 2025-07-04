package school.faang.user_service.dto.career;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CareerDto {
    private long id;
    private LocalDate from;
    private LocalDate to;
    private String company;
    private String position;
}
