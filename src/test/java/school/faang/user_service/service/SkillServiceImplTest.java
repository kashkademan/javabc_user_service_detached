package school.faang.user_service.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapperImpl skillMapper;

    @Mock
    private SkillCandidateMapperImpl skillCandidateMapper;

    @Mock
    private SkillOfferService skillOfferService;

    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @InjectMocks
    private SkillServiceImpl skillService;

    private static final long SKILL1_ID = 1L;
    private static final String SKILL1_TITLE = "Java";

    private static final long SKILL2_ID = 2L;
    private static final String SKILL2_TITLE = "Go";

    private static final long USER1_ID = 1L;

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
    @DisplayName("Skill creation test. Positive. Skill is created.")
    public void testCreate_created() {
        when(skillRepository.existsByTitle(skill1.getTitle())).thenReturn(false);
        when(skillMapper.toEntity(skill1Dto)).thenReturn(skill1);
        when(skillMapper.toDto(skill1)).thenReturn(skill1Dto);
        when(skillRepository.save(skill1)).thenReturn(skill1);

        SkillDto result = skillService.create(skill1Dto);

        assertEquals(result, skill1Dto);
    }

    @Test
    @DisplayName("Skill creation test. Negative. Exception should be thrown.")
    public void testCreate_notCreated() {
        when(skillRepository.existsByTitle(skill1.getTitle())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillService.create(skill1Dto));
    }

    @Test
    @DisplayName("Get users skills test.")
    public void testGetUserSkills() {
        when(skillRepository.findAllByUserId(USER1_ID)).thenReturn(List.of(skill1, skill2));
        when(skillMapper.toDto(skill1)).thenReturn(skill1Dto);
        when(skillMapper.toDto(skill2)).thenReturn(skill2Dto);

        List<SkillDto> result = skillService.getUserSkills(USER1_ID);

        assertEquals(result, List.of(skill1Dto, skill2Dto));
    }

    @Test
    @DisplayName("Get skills offered to user test.")
    public void testGetOfferedSkills() {
        SkillCandidateDto skillCandidateDto1 = new SkillCandidateDto(skill1Dto, 2L);
        SkillCandidateDto skillCandidateDto2 = new SkillCandidateDto(skill2Dto, 3L);
        
        when(skillRepository.findSkillsOfferedToUser(USER1_ID)).thenReturn(List.of(skill1, skill1, skill2, skill2, skill2));
        when(skillMapper.toDto(skill1)).thenReturn(skill1Dto);
        when(skillMapper.toDto(skill2)).thenReturn(skill2Dto);
        when(skillCandidateMapper.toDto(skill1Dto, 2L)).thenReturn(skillCandidateDto1);
        when(skillCandidateMapper.toDto(skill2Dto, 3L)).thenReturn(skillCandidateDto2);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(USER1_ID);
        
        assertEquals(result, List.of(skillCandidateDto1, skillCandidateDto2));
    }

    @Test
    @DisplayName("Acquire skill from offers test. Positive. Skill is acquired.")
    public void testAcquireSkillFromOffers_acquired() {
        when(skillRepository.findById(SKILL1_ID)).thenReturn(Optional.of(skill1));
        when(skillMapper.toDto(skill1)).thenReturn(skill1Dto);
    }

    @Test
    @DisplayName("Acquire skill from offers test. Negative. Exception should be thrown. Skill already assigned.")
    public void testAcquireSkillFromOffers_notAcquired_alreadyAssigned() {
        when(skillRepository.findUserSkill(SKILL1_ID, USER1_ID)).thenReturn(Optional.of(skill1));
        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL1_ID, USER1_ID));
    }

    @Test
    @DisplayName("Acquire skill from offers test. Negative. Exception should be thrown. Skill not found.")
    public void testAcquireSkillFromOffers_notAcquired_skillNotFound() {
        // SkillOffer skillOffer1 = new SkillOffer();
        // SkillOffer skillOffer2 = new SkillOffer();
        // SkillOffer skillOffer3 = new SkillOffer();
        // SkillOffer skillOffer4 = new SkillOffer();
        // Recommendation recommendation1 = new Recommendation();
        // recommendation1.setAuthor(new User());
        // recommendation1.setReceiver(new User());
        
        // skillOffer1.setSkill(skill1);
        // skillOffer2.setSkill(skill1);
        // skillOffer3.setSkill(skill1);
        // skillOffer4.setSkill(skill1);

        // skillOffer1.setRecommendation(recommendation1);
        // skillOffer2.setRecommendation(recommendation1);
        // skillOffer3.setRecommendation(recommendation1);
        // skillOffer4.setRecommendation(recommendation1);

        // when(skillRepository.findUserSkill(SKILL1_ID, USER1_ID)).thenReturn(Optional.empty());
        // when(skillOfferService.findAllOffersOfSkill(SKILL1_ID, USER1_ID)).thenReturn(List.of(
        //     skillOffer1, 
        //     skillOffer2, 
        //     skillOffer3, 
        //     skillOffer4
        // ));
        // doNothing().when(skillRepository).assignSkillToUser(SKILL1_ID, USER1_ID);
        // doNothing().when(userSkillGuaranteeService).saveAll(List.of(new UserSkillGuarantee()));
        when(skillRepository.findById(SKILL1_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL1_ID, USER1_ID));
    }

    @Test
    @DisplayName("Find all offeres of a skill to user.")
    public void testFindAllOffersOfSkill() {
        
    }
}