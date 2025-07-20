package school.faang.user_service.dto.career;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class CareerDto {
    private final long id;
    private final LocalDate from;
    private final LocalDate to;
    private final String company;
    private final String position;


}
