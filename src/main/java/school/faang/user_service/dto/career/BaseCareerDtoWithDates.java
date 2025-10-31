package school.faang.user_service.dto.career;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public abstract class BaseCareerDtoWithDates {
    private final LocalDate from;
    private final LocalDate to;
}