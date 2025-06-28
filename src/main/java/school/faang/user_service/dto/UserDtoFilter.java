package school.faang.user_service.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoFilter {
    @Size(max = 64)
    private String namePattern;
    @Size(max = 20)
    private String phonePattern;
    private int experienceMin;
    private int experienceMax;
}
