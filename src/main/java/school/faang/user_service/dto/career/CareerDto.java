package school.faang.user_service.dto.career;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Getter
public class CareerDto extends BaseCareerDtoWithDates {
    private final long id;
    private final long userId;
    private final String company;
    private final String position;

    @Builder
    public CareerDto(LocalDate from, LocalDate to,
                     long id,
                     long userId,
                     String company,
                     String position) {
        super(from, to);
        this.id = id;
        this.userId = userId;
        this.company = company;
        this.position = position;
    }
}