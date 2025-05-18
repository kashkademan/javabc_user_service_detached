package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillService skillService;
    private Skill skill;
    private List<Long> skillIds;
    private List<Long> userIds;

    @BeforeEach
    public void setUp() {
        skill = new Skill();
        skill.setId(5L);

        skillIds = List.of(1L, 2L);
        userIds = List.of(10L, 20L);
    }

    @Test
    public void testGetSkillByIdOrThrow_successfully() {

        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));

        Skill returnskill = skillService.getSkillByIdOrThrow(skill.getId());

        verify(skillRepository, times(1)).findById(skill.getId());
        assertEquals(skill.getId(), returnskill.getId());
    }

    @Test
    public void testGetSkillByIdOrThrow_skillNotFound() {
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> skillService.getSkillByIdOrThrow(skill.getId()));
        verify(skillRepository, times(1)).findById(skill.getId());
    }

    @Test
    void testAssignSkillsToUsers_noExistingLink() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        skillService.assignSkillsToUsers(skillIds, userIds);

        verify(skillRepository, times(skillIds.size() * userIds.size()))
                .assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void testAssignSkillsToUsers_allLinksExists() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.of(mock(Skill.class)));

        skillService.assignSkillsToUsers(skillIds, userIds);

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void testAssignSkillsToUsers_someLinksExists() {
        when(skillRepository.findUserSkill(skillIds.get(0), userIds.get(0)))
                .thenReturn(Optional.of(mock(Skill.class)));
        when(skillRepository.findUserSkill(skillIds.get(1), userIds.get(0)))
                .thenReturn(Optional.empty());
        when(skillRepository.findUserSkill(skillIds.get(0), userIds.get(1)))
                .thenReturn(Optional.empty());
        when(skillRepository.findUserSkill(skillIds.get(1), userIds.get(1)))
                .thenReturn(Optional.empty());

        skillService.assignSkillsToUsers(skillIds, userIds);

        verify(skillRepository, never()).assignSkillToUser(skillIds.get(0), userIds.get(0));
        verify(skillRepository, times(1)).assignSkillToUser(skillIds.get(1), userIds.get(0));
        verify(skillRepository, times(1)).assignSkillToUser(skillIds.get(0), userIds.get(1));
        verify(skillRepository, times(1)).assignSkillToUser(skillIds.get(1), userIds.get(1));
    }
}
