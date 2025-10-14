package school.faang.user_service.dto.career;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CareerDto(long id,
                        LocalDate from,
                        LocalDate to,
                        String company,
                        String position,
                        long userId) {
}