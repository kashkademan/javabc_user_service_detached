package school.faang.user_service.dto.career;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public abstract class BaseCareerDtoWithDates {
    private final LocalDate from;
    private final LocalDate to;
}