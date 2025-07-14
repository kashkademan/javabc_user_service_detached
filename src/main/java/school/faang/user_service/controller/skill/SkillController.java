package school.faang.user_service.controller.skill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillServiceImpl skillService;
    private final UserContext userContext;

    @Operation(summary = "Create a new skill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Skill created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    @PostMapping("/skill")
    public ResponseEntity<SkillDto> create(@RequestBody @Validated CreateSkillDto skillDto) {
        return new ResponseEntity<>(skillService.create(skillDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get skills by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skills retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found or no skills available for the user"),
    })
    @GetMapping("/skill/{userId}")
    public ResponseEntity<List<SkillDto>> getByUserId(@PathVariable("userId") @Validated @NotNull @NotBlank Long userId) {
        return new ResponseEntity<>(skillService.getByUserId(userId), HttpStatus.OK);
    }

    @Operation(summary = "Get offered skills for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offered skills retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No offered skills found for the user"),
    })
    @GetMapping("/skill/offers")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkills() {
        return new ResponseEntity<>(skillService.getOfferedSkills(userContext.getUserId()), HttpStatus.OK);
    }

    @Operation(summary = "Acquire a skill from offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skill acquired successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid skill ID or skill not available for acquisition"),
    })
    @PostMapping("/skill/acquire/{skillId}")
    public ResponseEntity<Void> acquireSkillFromOffers(@PathVariable @Validated @NotNull long skillId) {
        skillService.acquireSkillFromOffers(skillId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

}