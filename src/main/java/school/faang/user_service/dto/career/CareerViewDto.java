package school.faang.user_service.dto.career;

import java.time.LocalDate;

public record CareerViewDto(
        long id,
        LocalDate from,
        LocalDate to,
        String company,
        String position) {
}
