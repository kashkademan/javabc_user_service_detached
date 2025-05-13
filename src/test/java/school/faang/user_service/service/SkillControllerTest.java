package school.faang.user_service.service;

import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

public class SkillControllerTest {

    private static final long SKILL1_ID = 1L;
    private static final String SKILL1_TITLE = "Java";

    private static final long SKILL2_ID = 2L;
    private static final String SKILL2_TITLE = "Java";
    
    private static final long SKILL3_ID = 3L;
    private static final String SKILL3_TITLE = "    ";
    
    private static final long SKILL4_ID = 4L;
    private static final String SKILL4_TITLE = "";
    
    private static final long SKILL5_ID = 5L;
    private static final String SKILL5_TITLE = null; 
    
    private static final long SKILL6_ID = 6L;
    private static final String SKILL6_TITLE = "Java";

    private Skill skill1;
    private Skill skill2;
    private Skill skill3;
    private Skill skill4;
    private Skill skill5;
    private Skill skill6;

    private SkillDto skill1Dto;
}
