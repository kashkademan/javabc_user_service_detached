package school.faang.user_service.dto;

public record EducationAddRequestDto(Integer yearFrom, Integer yearTo,
                                     String institution, String educationLevel, String specialization) {
}
