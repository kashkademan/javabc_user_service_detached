package school.faang.user_service.dto.career;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateCareerDto(LocalDate from,
                              LocalDate to,
                              String company,
                              String position) {

}