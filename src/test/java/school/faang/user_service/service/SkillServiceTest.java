package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillService skillService;

    @Test
    void getSkillsByIds() {
        List<Long> skillIds = List.of(1L, 2L, 3L);
        List<Skill> skills = List.of(
                Skill.builder().id(1L).build(),
                Skill.builder().id(2L).build(),
                Skill.builder().id(3L).build()
        );

        when(skillRepository.getSkillsByIds(skillIds)).thenReturn(skills);

        List<Skill> resultSkills = skillService.getSkillsByIds(skillIds);
        assertNotNull(resultSkills);
        assertEquals(3, resultSkills.size());
    }
}