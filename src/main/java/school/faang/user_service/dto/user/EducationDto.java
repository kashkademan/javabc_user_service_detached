package school.faang.user_service.dto.user;

public record EducationDto(
        Long id,
        Integer yearFrom,
        Integer yearTo,
        String institution,
        String educationLevel,
        String specialization
) {
}
