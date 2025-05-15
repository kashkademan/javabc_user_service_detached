package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillService skillService;

    @Test
    public void testGetSkillByIdOrThrow_successfully() {
        long skillId = 5L;
        Skill skill = new Skill();
        skill.setId(skillId);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        Skill returnskill = skillService.getSkillByIdOrThrow(skillId);

        verify(skillRepository, times(1)).findById(skillId);
        assertEquals(skill.getId(), returnskill.getId());
    }

    @Test
    public void testGetSkillByIdOrThrow_skillNotFound() {
        long skillId = 5L;
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> skillService.getSkillByIdOrThrow(skillId));
        verify(skillRepository, times(1)).findById(skillId);
    }
}
