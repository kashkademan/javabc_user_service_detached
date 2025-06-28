package school.faang.user_service.service.Recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.builder.NotificationEventBuilder;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.Recommendation.RecommendationMapperImpl;
import school.faang.user_service.mapper.Recommendation.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private  NotificationEventBuilder eventBuilder;
    @Captor
    private ArgumentCaptor<UserSkillGuarantee> captorListUserSkillGuarantee;
    @Captor
    ArgumentCaptor<RecommendationDto> recommendationDtoArgumentCaptor;
    @Spy
    private RecommendationMapperImpl recommendationMapper;
    @Spy
    private SkillOfferMapperImpl skillOfferMapper;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(recommendationMapper, "skillOfferMapper", skillOfferMapper);
    }

    private static final long SKILL_ID = 1L;
    private static final long AUTHOR_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final String RECEIVER_NAME = "Kirill";
    private static final String AUTHOR_NAME = "Dima";
    private static final String CONTENT = "Test1";
    private static final String TITlE_SKILL = "Test";
    private static final long NEW_RECOMMENDATION_ID = 0;

    @Test
    public void createExistingReceiverSkills() {

        when(skillRepository.findUserSkill(SKILL_ID, RECEIVER_ID)).thenReturn(Optional.of(prepareDataSkill()));
        when(userRepository.getReferenceById(RECEIVER_ID)).thenReturn(prepareDataUser(RECEIVER_NAME, RECEIVER_ID));
        when(skillRepository.getReferenceById(SKILL_ID)).thenReturn(prepareDataSkill());
        when(userRepository.getReferenceById(AUTHOR_ID)).thenReturn(prepareDataUser(AUTHOR_NAME, AUTHOR_ID));
        when(recommendationRepository.findById(NEW_RECOMMENDATION_ID)).thenReturn(Optional.of(prepareDataRecommendation(AUTHOR_NAME, AUTHOR_ID, RECEIVER_NAME, RECEIVER_ID, CONTENT, 1)));

        RecommendationDto resultCreate = recommendationService.create(prepareDataRecommendationDto());
        verify(userSkillGuaranteeRepository).save(captorListUserSkillGuarantee.capture());

        UserSkillGuarantee resultUserSkillGuarantee = captorListUserSkillGuarantee.getValue();
        assertEquals(RECEIVER_NAME, resultUserSkillGuarantee.getUser().getUsername());
        assertEquals(TITlE_SKILL, resultUserSkillGuarantee.getSkill().getTitle());
        assertEquals(AUTHOR_NAME, resultUserSkillGuarantee.getGuarantor().getUsername());
        assertEquals(CONTENT, resultCreate.getContent());
        assertEquals(prepareDataListSkillOfferDto(1).size(), resultCreate.getSkillOffers().size());
    }

    @Test
    public void skillCreationBaseCase() {
        when(skillRepository.findUserSkill(SKILL_ID, RECEIVER_ID)).thenReturn(Optional.empty());
        when(recommendationRepository.findById(NEW_RECOMMENDATION_ID)).thenReturn(Optional.of(prepareDataRecommendation(AUTHOR_NAME, AUTHOR_ID, RECEIVER_NAME, RECEIVER_ID, CONTENT, 1)));
        recommendationService.create(prepareDataRecommendationDto());
        verify(skillOfferRepository).create(SKILL_ID, NEW_RECOMMENDATION_ID);
    }

    @Test
    public void testSaveRecommendation() {

        when(recommendationRepository.create(AUTHOR_ID, RECEIVER_ID, CONTENT)).thenReturn(NEW_RECOMMENDATION_ID);
        when(recommendationRepository.findById(NEW_RECOMMENDATION_ID)).thenReturn(Optional.ofNullable(
                prepareDataRecommendation(AUTHOR_NAME, AUTHOR_ID, RECEIVER_NAME, RECEIVER_ID, CONTENT, 1)));

        RecommendationDto result = recommendationService.create(prepareDataRecommendationDto());
        verify(skillOfferRepository).create(SKILL_ID, NEW_RECOMMENDATION_ID);
        assertEquals(AUTHOR_ID, result.getAuthorId());
        assertEquals(RECEIVER_ID, result.getReceiverId());
        assertEquals(1, result.getSkillOffers().size());
    }


    @Test
    void testSkillOfferToDto() {
        Skill skill = Skill.builder().id(1L).build();
        Recommendation recommendation = Recommendation.builder().id(2L).build();
        SkillOffer skillOffer = SkillOffer.builder().id(3L).skill(skill).recommendation(recommendation).build();

        SkillOfferDto dto = skillOfferMapper.toDto(skillOffer);

        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals(1L, dto.getSkillId());
        assertEquals(2L, dto.getRecommendationId());
    }

    @Test
    void testSkillOfferListToDtoList() {
        Skill skill = Skill.builder().id(1L).build();
        Recommendation recommendation = Recommendation.builder().id(2L).build();
        SkillOffer skillOffer = SkillOffer.builder().id(3L).skill(skill).recommendation(recommendation).build();

        List<SkillOfferDto> dtoList = skillOfferMapper.toDtoList(Collections.singletonList(skillOffer));

        assertNotNull(dtoList);
        assertEquals(1, dtoList.size());
        assertEquals(3L, dtoList.get(0).getId());
    }

    @Test
    void testRecommendationToDto() {
        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        Skill skill = Skill.builder().id(3L).build();

        Recommendation recommendation = Recommendation.builder()
                .id(4L)
                .author(author)
                .receiver(receiver)
                .content("Test content")
                .skillOffers(Collections.singletonList(
                        SkillOffer.builder().id(5L).skill(skill).recommendation(null).build()
                ))
                .build();

        RecommendationDto dto = recommendationMapper.toDto(recommendation);

        assertNotNull(dto);
        assertEquals(4L, dto.getId());
        assertEquals(1L, dto.getAuthorId());
        assertEquals(2L, dto.getReceiverId());
        assertEquals("Test content", dto.getContent());
        assertNotNull(dto.getSkillOffers());
        assertEquals(1, dto.getSkillOffers().size());
        assertEquals(5L, dto.getSkillOffers().get(0).getId());
    }

    private RecommendationDto prepareDataRecommendationDto() {
        return RecommendationDto.builder()
                .id(1L)
                .skillOffers(prepareDataListSkillOfferDto(1))
                .authorId(AUTHOR_ID)
                .receiverId(RECEIVER_ID)
                .content(CONTENT)
                .build();
    }

    private Recommendation prepareDataRecommendation(String authorName, long authorId, String receiverName, long receiverId, String content, int numberSkillOffers) {
        return Recommendation.builder()
                .id(1L)
                .author(prepareDataUser(authorName, authorId))
                .receiver(prepareDataUser(receiverName, receiverId))
                .content(content)
                .skillOffers(prepareDataListSkillOffer(numberSkillOffers))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<SkillOffer> prepareDataListSkillOffer(int numberSkillOffers) {
        List<SkillOffer> skillOffers = new ArrayList<>();
        for (int i = 0; i < numberSkillOffers; i++) {
            skillOffers.add(SkillOffer.builder()
                    .id(1L + i)
                    .skill(prepareDataSkill())
                    .recommendation(Recommendation.builder().id(NEW_RECOMMENDATION_ID).build())
                    .build());
        }
        return skillOffers;
    }

    private List<SkillOfferDto> prepareDataListSkillOfferDto(int numberSkillOffers) {
        List<SkillOfferDto> skillOfferDto = new ArrayList<>();
        for (int i = 0; i < numberSkillOffers; i++) {
            skillOfferDto.add(SkillOfferDto.builder()
                    .id(1L + i)
                    .skillId(1L + i)
                    .recommendationId(1L + i)
                    .build());
        }
        return skillOfferDto;
    }

    private User prepareDataUser(String name, Long userId) {
        return User.builder()
                .id(userId)
                .username(name)
                .build();
    }

    private Skill prepareDataSkill() {
        return Skill.builder()
                .id(1L)
                .title(TITlE_SKILL)
                .build();
    }
}
