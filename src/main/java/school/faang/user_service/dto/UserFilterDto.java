package school.faang.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    private String namePattern;
    private String phonePattern;
    private Integer experienceMin;
    private Integer experienceMax;
}
