package school.faang.user_service.controller.education;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping({"/api/v1/education"})
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @PostMapping
    @Operation(method = "POST", parameters = {
            @Parameter(name = "userId", required = true, description = "id пользователя")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Образование,"
                    + " добавляемое пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Образование успешно добавлено пользователем"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 403\n  "
                                                    + "\"message\":\"Текущему пользователю операция недоступна\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            }),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<EducationDto> addEducationToUser(@RequestParam Integer userId,
                                                           @RequestBody @Validated EducationDto educationDto) {
        EducationDto result = educationService.addEducation(userId, educationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @PutMapping
    @Operation(method = "PUT", parameters = {
            @Parameter(name = "educationId", required = true, description = "id образования")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Образование," +
                    " обновленное пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Образование успешно изменено пользователем"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 403\n  "
                                                    + "\"message\":\"Текущему пользователю операция недоступна\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            }),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<Void> updateEducation(@RequestParam Integer educationId,
                                                @RequestBody EducationDto educationDto) {
        educationService.updateEducation(educationId, educationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(method = "GET", parameters = {
            @Parameter(name = "educationId", required = true, description = "id образования")
    },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Образование успешно изменено пользователем"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 403\n  "
                                                    + "\"message\":\"Текущему пользователю операция недоступна\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            }),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<EducationDto> getEducation(@RequestParam Integer educationId) {
        EducationDto educationById = educationService.getEducationById(educationId);
        return ResponseEntity.ok(educationById);
    }
}
