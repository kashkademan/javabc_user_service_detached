package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoFilter {
    private String namePattern;
    private String phonePattern;
    private int experienceMin;
    private int experienceMax;
}
