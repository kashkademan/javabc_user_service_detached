package school.faang.user_service.dto.career;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CareerDto(long id,
                        LocalDate from,
                        LocalDate to,
                        String company,
                        String position
        /*long userId*/) {
}