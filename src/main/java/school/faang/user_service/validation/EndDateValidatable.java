package school.faang.user_service.validation;

import java.time.LocalDateTime;

public interface EndDateValidatable {
    LocalDateTime getStartDate();

    LocalDateTime getEndDate();
}
