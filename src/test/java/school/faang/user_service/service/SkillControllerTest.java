package school.faang.user_service.service;

import static org.mockito.Mockito.when;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    SkillService skillService;

    @InjectMocks
    SkillController skillController;

    @Test
    @Description("Skill created.")
    void testCreate_created() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");

        when(skillService.create(skillDto)).thenReturn(skillDto);

        assertEquals(skillController.create(skillDto), skillDto);
    }

    @Test
    @Description("Skill not created. Title contains only spaces.")
    void testCreate_notCreated_titleOnlySpaces() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("  ");
        
        assertThrows(
            DataValidationException.class, 
            () -> skillController.create(skillDto)
        );
    }

    @Test
    @Description("Skill not created. Title is empty string.")
    void testCreate_notCreated_titleEmptyString() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("");
        
        assertThrows(
            DataValidationException.class, 
            () -> skillController.create(skillDto)
        );
    }

    @Test
    @Description("Skill not created. Title is NULL.")
    void testCreate_notCreated_titleNull() {
        SkillDto skillDto = new SkillDto();
        
        assertThrows(
            DataValidationException.class, 
            () -> skillController.create(skillDto)
        );
    }

    @Test
    @Description("Retriev skills for user.")
    void testGetUserSkills_positive() {
        SkillDto skill1Dto = new SkillDto();
        skill1Dto.setTitle("Java");

        SkillDto skill2Dto = new SkillDto();
        skill2Dto.setTitle("Go");
        
        when(skillService.getUserSkills(1L)).thenReturn(List.of(skill1Dto, skill2Dto));
        
        assertEquals(List.of(skill1Dto, skill2Dto), skillController.getUserSkills(1L));
    }

    @Test
    @Description("Skills are not retried. User id is negative.")
    void testGetUserSkills_negative() {
        assertThrows(
            DataValidationException.class, 
            () -> skillController.getUserSkills(-1L)
        );
    }
}
