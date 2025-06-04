package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class SkillController {
    private final SkillService skillService;

    @Operation(
            summary = "Создание навыка",
            description = "Создает новый навык"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Навык успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/skill/create")
    public SkillDto create(@RequestBody @Valid SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @Operation(
            summary = "Навыки пользователя",
            description = "Вернет список пользователей"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Успешно",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/skill/{userId}/getUsersSkill")
    public List<SkillDto> getUserSkills(@PathVariable @Min(1) long userId) {
        return skillService.getUserSkills(userId);
    }

    @Operation(
            summary = "Предложение навыка пользователя",
            description = "Вернет список"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Навык успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/skill/{userId}/getOfferedSkills")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable @Min(1) long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PutMapping("/skill/{userId}/acquireSkillFromOffers")
    public SkillDto acquireSkillFromOffers(@PathVariable @Valid @Min(1) long skillId, @PathVariable @Valid @Min(1) long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}