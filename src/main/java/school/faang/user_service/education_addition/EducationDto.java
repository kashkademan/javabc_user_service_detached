package school.faang.user_service.education_addition;

import lombok.Data;

@Data
public class EducationDto {
    private long id;
    private Integer yearFrom;
    private Integer yearTo;
    private String institution;
    private String educationLevel;
    private String specialization;

}