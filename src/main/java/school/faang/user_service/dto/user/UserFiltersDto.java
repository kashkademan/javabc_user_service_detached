package school.faang.user_service.dto.user;

public record UserFiltersDto(
        String namePattern,
        String phoneNumber,
        Integer experienceMin,
        Integer experienceMax
) {
    public UserFiltersDto {
        if (namePattern == null || namePattern.isBlank()) namePattern = "";
        if (phoneNumber == null || phoneNumber.isBlank()) phoneNumber = "";
        if (experienceMin == null) experienceMin = 0;
        if (experienceMax == null) experienceMax = Integer.MAX_VALUE;
    }
}
