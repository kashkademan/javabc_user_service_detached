package school.faang.user_service.dto.user;

import lombok.Getter;

@Getter
public class EducationDto {

    long id;
    Integer yearFrom;
    Integer yearTo;
    String institution;
    String educationLevel;
    String specialization;
}
