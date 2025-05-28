package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.common.PreConditionFailedException;
import school.faang.user_service.exception.common.RecordNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.LogsConstants.CONDITION_FOR_OFFERS_AMOUNT_FAILED;
import static school.faang.user_service.util.LogsConstants.RECOMMENDATION_NOT_FOUND;
import static school.faang.user_service.util.LogsConstants.SKILL_NOT_FOUND;
import static school.faang.user_service.util.LogsConstants.USER_NOT_FOUND;
import static school.faang.user_service.util.SettingsConstants.MIN_SKILL_OFFERS;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillValidator skillValidator;
    @InjectMocks
    private SkillService skillService;
    private static final String TITLE = "Java";
    private static final long SKILL_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long SKILL_OFFER_ID = 3L;
    private static final long SKILL_OFFER_ID_WITHOUT_RECOMMENDATION = 4L;
    private static final long RECOMMENDATION_ID = 5L;

    private Skill skill;
    private List<Skill> skills;
    private User user;
    private SkillOffer skillOffer;
    private Recommendation recommendation;

    @BeforeEach
    public void setUp() {
        skill = Skill.builder()
                .id(SKILL_ID)
                .title(TITLE)
                .build();
        skills = List.of(skill);

        user = new User();
        user.setId(USER_ID);

        recommendation = new Recommendation();
        recommendation.setId(RECOMMENDATION_ID);
        skillOffer = SkillOffer.builder()
                .id(SKILL_OFFER_ID)
                .skill(skill)
                .recommendation(recommendation)
                .build();
    }

    @Test
    public void testCreateSkillShouldBeSuccessful() {
        Skill skillWithoutId = new Skill();
        skillWithoutId.setTitle(TITLE);
        when(skillRepository.save(skillWithoutId)).thenReturn(skill);

        Skill createdSkill = skillService.create(skillWithoutId);

        assertEquals(SKILL_ID, createdSkill.getId());
        assertEquals(TITLE, createdSkill.getTitle());
        verify(skillValidator).validateTitleUnique(TITLE);
        verify(skillRepository).save(skillWithoutId);
    }

    @Test
    public void testGetUserSkillsShouldBeSuccessful() {
        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(skills);

        List<Skill> userSkills = skillService.getUserSkills(USER_ID);

        assertEquals(skills, userSkills);
        verify(skillRepository).findAllByUserId(USER_ID);
    }

    @Test
    public void testGetUserSkillsShouldReturnEmptyList() {
        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());

        List<Skill> userSkills = skillService.getUserSkills(USER_ID);

        assertTrue(userSkills.isEmpty());
        verify(skillRepository).findAllByUserId(USER_ID);
    }

    @Test
    public void testGetOfferedSkillsShouldBeSuccessful() {
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);

        List<Skill> offeredSkills = skillService.getOfferedSkills(USER_ID);

        assertEquals(skills, offeredSkills);
        verify(skillRepository).findSkillsOfferedToUser(USER_ID);
    }

    @Test
    public void testGetOfferedSkillsShouldReturnEmptyList() {
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(Collections.emptyList());

        List<Skill> offeredSkills = skillService.getOfferedSkills(USER_ID);

        assertTrue(offeredSkills.isEmpty());
        verify(skillRepository).findSkillsOfferedToUser(USER_ID);
    }


    @Test
    public void testAcquireSkillFromOffersShouldBeSuccessful() {
        List<SkillOffer> skillOfferListSizeMinValid = Collections.nCopies(MIN_SKILL_OFFERS, skillOffer);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(skillOfferListSizeMinValid);

        Skill resultSkill = skillService.acquireSkillFromOffers(USER_ID, SKILL_ID);

        assertEquals(skill, resultSkill);
        verify(skillValidator).validateUserHasSkill(USER_ID, SKILL_ID);
        verify(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);
        verify(userSkillGuaranteeRepository, times(MIN_SKILL_OFFERS)).save(any(UserSkillGuarantee.class));
    }

    @Test
    public void testAcquireSkillFromOffersWhenSkillNotFound() {
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        RecordNotFoundException recordNotFoundException =
                assertThrows(RecordNotFoundException.class, () -> skillService.acquireSkillFromOffers(USER_ID, SKILL_ID));
        assertEquals(String.format(SKILL_NOT_FOUND, SKILL_ID), recordNotFoundException.getMessage());
    }

    @Test
    public void testAcquireSkillFromOffersWhenUserNotFound() {
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        RecordNotFoundException recordNotFoundException =
                assertThrows(RecordNotFoundException.class, () -> skillService.acquireSkillFromOffers(USER_ID, SKILL_ID));
        assertEquals(String.format(USER_NOT_FOUND, USER_ID), recordNotFoundException.getMessage());
    }

    @Test
    public void testAcquireSkillFromOffersWhenRecommendationIsNull() {
        SkillOffer skillOfferWithoutRecommendation;
        skillOfferWithoutRecommendation = SkillOffer.builder()
                .id(SKILL_OFFER_ID_WITHOUT_RECOMMENDATION)
                .skill(skill)
                .build();
        List<SkillOffer> skillOfferListSizeMinValidWithoutRecommendation =
                new ArrayList<>(Collections.nCopies(MIN_SKILL_OFFERS, skillOffer));
        skillOfferListSizeMinValidWithoutRecommendation.set(MIN_SKILL_OFFERS - 1, skillOfferWithoutRecommendation);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).
                thenReturn(skillOfferListSizeMinValidWithoutRecommendation);

        RecordNotFoundException recordNotFoundException =
                assertThrows(RecordNotFoundException.class, () -> skillService.acquireSkillFromOffers(USER_ID, SKILL_ID));
        assertEquals(String.format(RECOMMENDATION_NOT_FOUND), recordNotFoundException.getMessage());
    }

    @Test
    public void testAcquireSkillFromOffersListSizeIsLessThan3() {
        List<SkillOffer> skillOfferListSizeNotValid = List.of(skillOffer);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(skillOfferListSizeNotValid);

        PreConditionFailedException preConditionFailedException =
                assertThrows(PreConditionFailedException.class, () -> skillService.acquireSkillFromOffers(USER_ID, SKILL_ID));
        assertEquals(String.format(CONDITION_FOR_OFFERS_AMOUNT_FAILED), preConditionFailedException.getMessage());
    }
}
