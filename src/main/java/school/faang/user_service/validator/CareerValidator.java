package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CareerValidator {
    public void validateDate(CareerDto careerDto) {
        if (careerDto.getDateFrom() == null) {
            throw new DataValidationException("Start date cannot be null");
        }

        if (careerDto.getDateFrom() != null && careerDto.getDateFrom().isAfter(LocalDate.now())) {
            throw new DataValidationException(
                    String.format("Start date cannot be in future. Provided: %s", careerDto.getDateFrom())
            );
        }

        if (careerDto.getDateTo() != null && careerDto.getDateTo().isAfter(LocalDate.now())) {
            throw new DataValidationException(
                    String.format("End date cannot be in future. Provided: %s", careerDto.getDateFrom())
            );
        }

        if (careerDto.getDateTo() != null && careerDto.getDateTo().isBefore(careerDto.getDateFrom())) {
            throw new DataValidationException(
                    String.format("End date (%s) cannot be before start date (%s)",
                            careerDto.getDateTo(), careerDto.getDateFrom())
            );
        }
    }
}
