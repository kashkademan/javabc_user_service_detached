package school.faang.user_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/skill")
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@RequestBody SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    @GetMapping("/forUser/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        if (userId < 0) {
            throw new DataValidationException("Invalid user id.");
        }
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/allOfferedToUser/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/acquireFromOffers")
    public SkillDto acquireSkillFromOffers(@RequestBody SkillAcquireDto skillAcquireDTO) {
        return skillService.acquireSkillFromOffers(skillAcquireDTO.getSkillId(), skillAcquireDTO.getUserId());
    }

    @GetMapping("/offersOfSkill/{skillId}/forUser/{userId}")
    public List<SkillOfferDto> findSkillOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.findAllOffersOfSkill(skillId, userId);
    }

    private void validateSkill(SkillDto skill) {
        if (skill == null || skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("The skill is not valid.");
        }
    }
}
