package school.faang.user_service.service;

import static org.mockito.Mockito.when;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    private static final long SKILL1_ID = 1L;
    private static final String SKILL1_TITLE = "Java";

    private static final long SKILL2_ID = 2L;
    private static final String SKILL2_TITLE = "Go";
    
    private static final long SKILL3_ID = 3L;
    private static final String SKILL3_TITLE = "    ";
    
    private static final long SKILL4_ID = 4L;
    private static final String SKILL4_TITLE = "";
    
    private static final long SKILL5_ID = 5L;
    private static final String SKILL5_TITLE = null; 
    
    private static final long USER1_ID = 1L;
    private static final long USER2_ID = -6L;

    private SkillDto skill1Dto;
    private SkillDto skill2Dto;
    private SkillDto skill3Dto;
    private SkillDto skill4Dto;
    private SkillDto skill5Dto;

    @Mock
    SkillService skillService;

    @InjectMocks
    SkillController skillController;

    @BeforeEach
    void setUp() {
        skill1Dto = new SkillDto();
        skill1Dto.setId(SKILL1_ID);
        skill1Dto.setTitle(SKILL1_TITLE);
        
        skill2Dto = new SkillDto();
        skill2Dto.setId(SKILL2_ID);
        skill2Dto.setTitle(SKILL2_TITLE);
        
        skill3Dto = new SkillDto();
        skill3Dto.setId(SKILL3_ID);
        skill3Dto.setTitle(SKILL3_TITLE);
        
        skill4Dto = new SkillDto();
        skill4Dto.setId(SKILL4_ID);
        skill4Dto.setTitle(SKILL4_TITLE);
        
        skill5Dto = new SkillDto();
        skill5Dto.setId(SKILL5_ID);
        skill5Dto.setTitle(SKILL5_TITLE);
    }

    @Test
    void testCreate_created() {
        when(skillService.create(skill1Dto)).thenReturn(skill1Dto);
        assertEquals(skillController.create(skill1Dto), skill1Dto);
    }

    @Test
    void testCreate_notCreated_titleOnlySpaces() {
        assertThrows(DataValidationException.class, () -> skillController.create(skill3Dto));
    }

    @Test
    void testCreate_notCreated_titleEmptyString() {
        assertThrows(DataValidationException.class, () -> skillController.create(skill4Dto));
    }

    @Test
    void testCreate_notCreated_titleNull() {
        assertThrows(DataValidationException.class, () -> skillController.create(skill5Dto));
    }

    @Test
    void testGetUserSkills_positive() {
        when(skillService.getUserSkills(USER1_ID)).thenReturn(List.of(skill1Dto, skill2Dto));
        assertEquals(skillController.getUserSkills(USER1_ID), List.of(skill1Dto, skill2Dto));
    }

    @Test
    void testGetUserSkills_negative() {
        assertThrows(DataValidationException.class, () -> skillController.getUserSkills(USER2_ID));
    }
}
