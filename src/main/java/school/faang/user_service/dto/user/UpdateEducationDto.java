package school.faang.user_service.dto.user;

public record UpdateEducationDto(
        Integer yearFrom,
        Integer yearTo,
        String institution,
        String educationLevel,
        String specialization
) {
}
