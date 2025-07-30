package school.faang.user_service.controller.skill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@Validated
@Tag(name = "Навыки", description = "Операции с навыками пользователя")
public class SkillController {
    private final SkillService service;
    private final UserContext context;

    @PostMapping
    @Operation(summary = "Создать навык", description = "Создаёт новый навык на основе переданных данных")
    public ResponseEntity<SkillViewDto> create(@Valid @RequestBody SkillCreateDto skillDto) {
        SkillViewDto createdSkill = service.create(skillDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdSkill);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получить навыки пользователя", description = "Возвращает список навыков по ID пользователя")
    public ResponseEntity<List<SkillViewDto>> getByUserId(@PathVariable Long userId) {
        List<SkillViewDto> skills = service.getByUserId(userId);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/offered")
    @Operation(summary = "Получить предлагаемые навыки", description = "Возвращает список навыков, которые можно приобрести")
    public ResponseEntity<List<SkillOfferDto>> getOfferedSkills() {
        List<SkillOfferDto> skills = service.getOfferedSkills(context.getUserId());
        return ResponseEntity.ok(skills);
    }

    @PutMapping("/{skillId}")
    @Operation(summary = "Приобрести навык", description = "Добавляет навык из предложенных пользователю")
    public ResponseEntity<Void> acquireSkillFromOffers(@PathVariable Long skillId) {
        service.acquireSkillFromOffers(skillId, context.getUserId());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}