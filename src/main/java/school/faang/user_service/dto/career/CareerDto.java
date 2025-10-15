package school.faang.user_service.dto.career;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CareerDto extends BaseCareerDtoWithDates {
    private final long id;
    private final long userId;
    private final String company;
    private final String position;

    @Builder
    public CareerDto(long id,
                     long userId,
                     LocalDate from,
                     LocalDate to,
                     String company,
                     String position) {
        super(from, to);
        this.id = id;
        this.company = company;
        this.position = position;
        this.userId = userId;
    }
}