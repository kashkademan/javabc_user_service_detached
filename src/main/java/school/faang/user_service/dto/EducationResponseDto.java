package school.faang.user_service.dto;

public record EducationResponseDto(Long id, Integer yearFrom, Integer yearTo,
                                   String institution, String educationLevel, String specialization) {
}