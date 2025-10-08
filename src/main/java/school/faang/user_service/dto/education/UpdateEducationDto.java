package school.faang.user_service.dto.education;

public record UpdateEducationDto(
        Integer yearFrom,
        Integer yearTo,
        String institution,
        String educationLevel,
        String specialization
) {

}
