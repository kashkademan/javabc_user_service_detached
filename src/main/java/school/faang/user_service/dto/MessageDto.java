package school.faang.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageDto(
        @Schema(
                description = "message description"
        )
        String message
) {}
