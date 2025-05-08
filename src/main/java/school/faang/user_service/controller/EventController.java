package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.event.EventDto;

@RequestMapping("/events")
@Tag(name = "События", description = "API для работы с событиями")
public interface EventController {
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие успешно создано",
                    content = @Content(schema = @Schema(implementation = EventDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные события"),
            @ApiResponse(responseCode = "403", description = "Недостаточно навыков для создания события"),
            @ApiResponse(responseCode = "404", description = "Пользователь-создатель не найден")
    })
    @PostMapping
    ResponseEntity<EventDto> create(@RequestBody @Valid EventDto event);
}
