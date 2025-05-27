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
    private static final String title = "Java";
    private static final long skillId = 1L;
    private static final long userId = 2L;
    private static final long SkillOfferId = 3L;
    private static final long SkillOfferIdWithoutRecommendation = 4L;
    private static final long RecommendationId = 5L;

    private Skill skill;
    private List<Skill> skills;
    private User user;
    private SkillOffer skillOffer;
    private Recommendation recommendation;

    @BeforeEach
    public void setUp() {
        skill = Skill.builder()
                .id(skillId)
                .title(title)
                .build();
        skills = List.of(skill);

        user = new User();
        user.setId(userId);

        recommendation = new Recommendation();
        recommendation.setId(RecommendationId);
        skillOffer = SkillOffer.builder()
                .id(SkillOfferId)
                .skill(skill)
                .recommendation(recommendation)
                .build();
    }

    @Test
    public void createSkillShouldBeSuccessful() {
        Skill skillWithoutId = new Skill();
        skillWithoutId.setTitle(title);
        when(skillRepository.save(skillWithoutId)).thenReturn(skill);

        Skill createdSkill = skillService.create(skillWithoutId);

        assertEquals(skillId, createdSkill.getId());
        assertEquals(title, createdSkill.getTitle());
        verify(skillValidator).validateTitleUnique(title);
    }

    @Test
    public void getUserSkillsShouldBeSuccessful() {
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        List<Skill> userSkills = skillService.getUserSkills(userId);

        assertEquals(skills, userSkills);
        verify(skillRepository).findAllByUserId(userId);
    }

    @Test
    public void getUserSkillsShouldReturnEmptyList() {
        when(skillRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<Skill> userSkills = skillService.getUserSkills(userId);

        assertTrue(userSkills.isEmpty());
        verify(skillRepository).findAllByUserId(userId);
    }

    @Test
    public void getOfferedSkillsShouldBeSuccessful() {
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);

        List<Skill> offeredSkills = skillService.getOfferedSkills(userId);

        assertEquals(skills, offeredSkills);
        verify(skillRepository).findSkillsOfferedToUser(userId);
    }

    @Test
    public void getOfferedSkillsShouldReturnEmptyList() {
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(Collections.emptyList());

        List<Skill> offeredSkills = skillService.getOfferedSkills(userId);

        assertTrue(offeredSkills.isEmpty());
        verify(skillRepository).findSkillsOfferedToUser(userId);
    }


    @Test
    public void acquireSkillFromOffersShouldBeSuccessful() {
        List<SkillOffer> skillOfferListSizeMinValid = Collections.nCopies(MIN_SKILL_OFFERS, skillOffer);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOfferListSizeMinValid);

        Skill resultSkill = skillService.acquireSkillFromOffers(userId, skillId);

        assertEquals(skill, resultSkill);
        verify(skillValidator).validateUserHasSkill(userId, skillId);
        verify(skillRepository).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeRepository, times(MIN_SKILL_OFFERS)).save(any(UserSkillGuarantee.class));
    }

    @Test
    public void acquireSkillFromOffersWhenSkillNotFound() {
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        RecordNotFoundException recordNotFoundException =
                assertThrows(RecordNotFoundException.class, () -> skillService.acquireSkillFromOffers(userId, skillId));
        assertEquals(String.format(SKILL_NOT_FOUND, skillId), recordNotFoundException.getMessage());
    }

    @Test
    public void acquireSkillFromOffersWhenUserNotFound() {
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RecordNotFoundException recordNotFoundException =
                assertThrows(RecordNotFoundException.class, () -> skillService.acquireSkillFromOffers(userId, skillId));
        assertEquals(String.format(USER_NOT_FOUND, userId), recordNotFoundException.getMessage());
    }

    @Test
    public void acquireSkillFromOffersWhenRecommendationIsNull() {
        SkillOffer skillOfferWithoutRecommendation;
        skillOfferWithoutRecommendation = SkillOffer.builder()
                .id(SkillOfferIdWithoutRecommendation)
                .skill(skill)
                .build();
        List<SkillOffer> skillOfferListSizeMinValidWithoutRecommendation =
                new ArrayList<>(Collections.nCopies(MIN_SKILL_OFFERS, skillOffer));
        skillOfferListSizeMinValidWithoutRecommendation.set(MIN_SKILL_OFFERS - 1, skillOfferWithoutRecommendation);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).
                thenReturn(skillOfferListSizeMinValidWithoutRecommendation);

        RecordNotFoundException recordNotFoundException =
                assertThrows(RecordNotFoundException.class, () -> skillService.acquireSkillFromOffers(userId, skillId));
        assertEquals(String.format(RECOMMENDATION_NOT_FOUND), recordNotFoundException.getMessage());
    }

    @Test
    public void acquireSkillFromOffersListSizeIsLessThan3() {
        List<SkillOffer> skillOfferListSizeNotValid = List.of(skillOffer);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOfferListSizeNotValid);

        PreConditionFailedException preConditionFailedException =
                assertThrows(PreConditionFailedException.class, () -> skillService.acquireSkillFromOffers(userId, skillId));
        assertEquals(String.format(CONDITION_FOR_OFFERS_AMOUNT_FAILED), preConditionFailedException.getMessage());
    }
}
