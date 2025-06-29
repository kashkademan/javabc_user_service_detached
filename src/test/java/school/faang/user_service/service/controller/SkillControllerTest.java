package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    public void testCreateWithNullableTitle() {
        SkillDto skill = new SkillDto();
        assertThrows(DataValidationException.class, () -> {
            skillController.create(skill);
        });
    }

    @Test
    public void testCreateWithEmptyTitle() {
        SkillDto skill = new SkillDto();
        skill.setTitle("");
        assertThrows(DataValidationException.class, () -> {
            skillController.create(skill);
        });
    }

    @Test
    public void testCreateBlankTitle() {
        SkillDto skill = new SkillDto();
        skill.setTitle("   ");
        assertThrows(DataValidationException.class, () -> {
            skillController.create(skill);
        });
    }

    @Test
    public void testCreate() {
        SkillDto skill = new SkillDto();
        skill.setTitle("title");
        skillController.create(skill);
        verify(skillService, times(1)).create(skill);
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1L;
        SkillDto dto = new SkillDto();
        dto.setId(100L);
        dto.setTitle("Java");

        when(skillService.getUserSkills(userId)).thenReturn(List.of(dto));

        List<SkillDto> result = skillController.getUserSkills(userId);

        verify(skillService).getUserSkills(userId);
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getTitle());
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 2L;
        SkillDto dto = new SkillDto();
        dto.setId(101L);
        dto.setTitle("Python");

        SkillCandidateDto candidateDto = new SkillCandidateDto(dto, 3L);

        when(skillService.getOfferedSkills(userId)).thenReturn(List.of(candidateDto));

        List<SkillCandidateDto> result = skillController.getOfferedSkills(userId);

        verify(skillService).getOfferedSkills(userId);
        assertEquals(1, result.size());
        assertEquals("Python", result.get(0).getSkill().getTitle());
        assertEquals(3L, result.get(0).getOffersAmount());
    }

    @Test
    public void testAcquireSkillFromOffers() {
        long skillId = 10L;
        long userId = 5L;
        SkillDto dto = new SkillDto();
        dto.setId(skillId);
        dto.setTitle("Kotlin");

        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(dto);

        SkillDto result = skillController.acquireSkillFromOffers(skillId, userId);

        verify(skillService).acquireSkillFromOffers(skillId, userId);
        assertEquals("Kotlin", result.getTitle());
        assertEquals(skillId, result.getId());
    }
}
