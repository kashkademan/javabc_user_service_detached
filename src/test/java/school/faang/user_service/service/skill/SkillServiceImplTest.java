package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Spy
    private SkillMapper skillMapper;
    @Spy
    private SkillCandidateMapper skillCandidateMapper;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper;
    @InjectMocks
    private SkillServiceImpl skillService;

    private static final String SKILL_NAME = "Java";
    private static final Long SKILL_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "Pavel";
    private static final String USER_EMAIL = "test@test.ru";
    private static final String USER_PHONE = "79524478814";
    private static final String ABOUT_USER = "About user";
    private static final int OFFERS_AMOUNT = 5;
    private static final int FEW_SKILLS = 1;
    private static final int MIN_OFFERS_FOR_ACQUIRE = 3;

    @Test
    void create_whenSkillAlreadyExists_shouldThrowException() {
        CreateSkillDto createSkillDto = new CreateSkillDto(SKILL_NAME);
        when(skillRepository.existsByTitle(SKILL_NAME)).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> skillService.create(createSkillDto));

        verify(skillRepository, never()).save(any());
    }

    @Test
    void create_shouldSaveAndReturnDto_whenSkillDoesNotExist() {
        CreateSkillDto createSkillDto = new CreateSkillDto(SKILL_NAME);
        Skill skill = new Skill();

        Skill savedSkill = new Skill();
        savedSkill.setId(SKILL_ID);
        savedSkill.setTitle(SKILL_NAME);
        savedSkill.setCreatedAt(LocalDateTime.now());
        savedSkill.setUpdatedAt(LocalDateTime.now());

        SkillDto expectedDto = new SkillDto(
                SKILL_ID,
                SKILL_NAME,
                savedSkill.getCreatedAt(),
                savedSkill.getUpdatedAt(),
                List.of()
        );

        when(skillRepository.existsByTitle(SKILL_NAME)).thenReturn(false);
        when(skillMapper.toSkill(createSkillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(savedSkill);
        when(skillMapper.toSkillDto(savedSkill)).thenReturn(expectedDto);

        SkillDto result = skillService.create(createSkillDto);

        assertEquals(expectedDto, result);

        verify(skillRepository).existsByTitle(SKILL_NAME);
        verify(skillMapper).toSkill(createSkillDto);
        verify(skillRepository).save(skill);
        verify(skillMapper).toSkillDto(savedSkill);
    }

    @Test
    void getByUserId_shouldReturnSkillsWithGuarantors_whenUserHasSkills() {
        Skill skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle(SKILL_NAME);
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());

        List<Skill> skills = List.of(skill);

        UserDto userDto = new UserDto(USER_ID, USER_NAME, USER_EMAIL, USER_PHONE, ABOUT_USER);
        User guarantor = new User();
        UserSkillGuarantee guarantee = new UserSkillGuarantee();
        guarantee.setGuarantor(guarantor);

        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(skills);
        when(userSkillGuaranteeRepository.findAllByUserIdAndSkillId(USER_ID, SKILL_ID))
                .thenReturn(List.of(guarantee));
        when(userMapper.toUserDto(guarantor)).thenReturn(userDto);

        List<SkillDto> result = skillService.getByUserId(USER_ID);

        SkillDto expected = new SkillDto(
                SKILL_ID,
                SKILL_NAME,
                skill.getCreatedAt(),
                skill.getUpdatedAt(),
                List.of(userDto)
        );

        assertEquals(List.of(expected), result);

        verify(skillRepository).findAllByUserId(USER_ID);
        verify(userSkillGuaranteeRepository).findAllByUserIdAndSkillId(USER_ID, SKILL_ID);
        verify(userMapper).toUserDto(guarantor);
    }

    @Test
    void getOfferedSkills_shouldReturnOfferedSkills_whenUserHasOfferedSkills() {
        Skill skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle(SKILL_NAME);
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());

        List<Skill> offeredSkills = List.of(skill);

        UserDto userDto = new UserDto(USER_ID, USER_NAME, USER_EMAIL, USER_PHONE, ABOUT_USER);

        SkillDto skillDto = new SkillDto(
                SKILL_ID,
                SKILL_NAME,
                skill.getCreatedAt(),
                skill.getUpdatedAt(),
                List.of(userDto)
        );

        User guarantor = new User();
        UserSkillGuarantee guarantee = new UserSkillGuarantee();
        guarantee.setGuarantor(guarantor);
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillDto, OFFERS_AMOUNT);

        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(offeredSkills);
        when(skillOfferRepository.countAllOffersOfSkill(SKILL_ID, USER_ID))
                .thenReturn(OFFERS_AMOUNT);
        when(userSkillGuaranteeRepository.findAllByUserIdAndSkillId(USER_ID, SKILL_ID))
                .thenReturn(List.of(guarantee));
        when(userMapper.toUserDto(guarantor)).thenReturn(userDto);
        when(skillCandidateMapper.toDto(skillDto, OFFERS_AMOUNT)).thenReturn(skillCandidateDto);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(USER_ID);

        assertEquals(List.of(skillCandidateDto), result);

        verify(skillRepository).findSkillsOfferedToUser(USER_ID);
        verify(skillOfferRepository).countAllOffersOfSkill(SKILL_ID, USER_ID);
        verify(userSkillGuaranteeRepository).findAllByUserIdAndSkillId(USER_ID, SKILL_ID);
        verify(userMapper).toUserDto(guarantor);
        verify(skillCandidateMapper).toDto(skillDto, OFFERS_AMOUNT);
    }

    @Test
    void acquireSkillFromOffers_whenUserAlreadyHasSkill_shouldThrowException() {
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID))
                .thenReturn(Optional.of(new Skill()));

        assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).saveAll(anyList());
    }

    @Test
    void acquireSkillFromOffers_whenUserLacksSkill_shouldThrowException() {
        ReflectionTestUtils.setField(skillService, "minOffersForAcquire", MIN_OFFERS_FOR_ACQUIRE);

        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.countAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(FEW_SKILLS);

        DataValidationException expect = assertThrows(
                DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)
        );

        assertTrue(expect.getMessage().contains("Недостаточно рекомендаций"));

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).saveAll(anyList());
    }

    @Test
    void acquireSkillFromOffers_whenUserNotFound_shouldThrowException() {
        ReflectionTestUtils.setField(skillService, "minOffersForAcquire", MIN_OFFERS_FOR_ACQUIRE);

        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.countAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(MIN_OFFERS_FOR_ACQUIRE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException expect = assertThrows(
                EntityNotFoundException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)
        );

        assertTrue(expect.getMessage().contains("Пользователь не найден"));

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).saveAll(anyList());
    }

    @Test
    void acquireSkillFromOffers_whenSkillNotFound_shouldThrowException() {
        ReflectionTestUtils.setField(skillService, "minOffersForAcquire", MIN_OFFERS_FOR_ACQUIRE);

        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.countAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(MIN_OFFERS_FOR_ACQUIRE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new User()));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        EntityNotFoundException expect = assertThrows(
                EntityNotFoundException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)
        );

        assertTrue(expect.getMessage().contains("Умение не найдено"));

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).saveAll(anyList());
    }

    @Test
    void acquireSkillFromOffers_shouldAssignSkillAndSaveGuarantors_whenAllConditionsMet() {
        ReflectionTestUtils.setField(skillService, "minOffersForAcquire", MIN_OFFERS_FOR_ACQUIRE);

        User user = new User();
        Skill skill = new Skill();
        User guarantor1 = new User();
        User guarantor2 = new User();

        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.countAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(MIN_OFFERS_FOR_ACQUIRE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));

        Recommendation rec1 = mock(Recommendation.class);
        when(rec1.getAuthor()).thenReturn(guarantor1);
        SkillOffer offer1 = mock(SkillOffer.class);
        when(offer1.getRecommendation()).thenReturn(rec1);

        Recommendation rec2 = mock(Recommendation.class);
        when(rec2.getAuthor()).thenReturn(guarantor2);
        SkillOffer offer2 = mock(SkillOffer.class);
        when(offer2.getRecommendation()).thenReturn(rec2);

        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID))
                .thenReturn(List.of(offer1, offer2));

        skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        verify(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);

        verify(userSkillGuaranteeRepository).saveAll(argThat((List<UserSkillGuarantee> guarantees) -> {
            if (guarantees.size() != 2) return false;
            return guarantees.stream().allMatch(g ->
                    g.getUser() == user &&
                            g.getSkill() == skill &&
                            (g.getGuarantor() == guarantor1 || g.getGuarantor() == guarantor2)
            );
        }));
    }
}
