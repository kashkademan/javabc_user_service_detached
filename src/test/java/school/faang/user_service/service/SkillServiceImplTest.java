package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;

    private static final long SKILL1_ID = 1L;
    private static final String SKILL1_TITLE = "Java";

    private static final long SKILL2_ID = 2L;
    private static final String SKILL2_TITLE = "Go";

    private Skill skill1;
    private Skill skill2;
    private SkillDto skill1Dto;
    private SkillDto skill2Dto;

    @BeforeEach
    void setUp() {
        skill1 = Skill.builder()
          .id(SKILL1_ID)
          .title(SKILL1_TITLE)
          .build();
        
        skill2 = Skill.builder()
          .id(SKILL2_ID)
          .title(SKILL2_TITLE)
          .build();
        
        skill1Dto = new SkillDto(SKILL1_ID, SKILL1_TITLE);
        skill2Dto = new SkillDto(SKILL2_ID, SKILL2_TITLE);
    }

    @Test
    @DisplayName("Skill creation test. Positive. Skill created.")
    public void testCreate_created() {
        when(skillRepository.save(skill1)).thenReturn(skill1);
        when(skillMapper.toDto(skill1)).thenReturn(skill1Dto);
        when(skillMapper.toEntity(skill1Dto)).thenReturn(skill1);

        SkillDto result = skillService.create(skill1Dto);

        assertEquals(result, skill1Dto);
    }

    @Test
    @DisplayName("Skill creation test. Negative. Skill exists.")
    public void testCreate_notCreated() {

    }

    @Test
    @DisplayName("Get users skills test.")
    public void testGetUserSkills() {

    }

    @Test
    @DisplayName("Get skills offered to user test.")
    public void testGetOfferedSkills() {
        
    }

    @Test
    @DisplayName("Acquire skill from offers test.")
    public void testAcquireSkillFromOffers() {

    }

    @Test
    @DisplayName("Find all offeres of a skill to user.")
    public void testFindAllOffersOfSkill() {
        
    }
}
