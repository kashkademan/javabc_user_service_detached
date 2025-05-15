package school.faang.user_service.dto.EducationDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EducationDto {

    private long id;
    private Integer yearFrom;
    private Integer yearTo;
    private String institution;
    private String educationLevel;
    private String specialization;

}
