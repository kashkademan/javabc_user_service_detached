package school.faang.user_service.dto.user;

import lombok.Getter;

@Getter
public class UserFiltersDto {
    private String namePattern;
    private String phonePattern;
    private int experienceMin;
    private int experienceMax;
}
