package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {
    private static final long EXPECTED_DTO_ID = 1L;
    private static final long AUTHOR_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long SKILL_ID = 101L;
    private static final long SKILL_OFFER_ID = 1L;
    private Recommendation recommendation;
    private RecommendationDto inputDto;
    private RecommendationDto expectedDto;
    private SkillOfferDto skillOfferDto;
    private List<UserSkillGuarantee> guarantees = new ArrayList<>();

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillOfferMapperImpl skillOfferMapper;

    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @BeforeEach
    void init() {
        Skill skill = Skill.builder().id(SKILL_ID).guarantees(guarantees).build();
        SkillOffer skillOffer = SkillOffer.builder().id(SKILL_OFFER_ID).skill(skill).build();
        skillOfferDto = SkillOfferDto.builder().id(1L).skillId(SKILL_ID).build();
        User author = User.builder().id(AUTHOR_ID).build();
        User receiver = User.builder().id(RECEIVER_ID).skills(List.of(skill)).build();
        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .guarantor(author)
                .user(receiver)
                .skill(skill)
                .build();

        guarantees.add(guarantee);

        recommendation = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("Good worker")
                .skillOffers(List.of(skillOffer))
                .createdAt(LocalDateTime.now())
                .build();

        inputDto = RecommendationDto.builder()
                .authorId(AUTHOR_ID)
                .receiverId(RECEIVER_ID)
                .content("Good worker")
                .skillOffers(List.of(skillOfferDto))
                .build();

        expectedDto = RecommendationDto.builder()
                .id(EXPECTED_DTO_ID)
                .authorId(AUTHOR_ID)
                .receiverId(RECEIVER_ID)
                .content(recommendation.getContent())
                .skillOffers(List.of(skillOfferDto))
                .createdAt(recommendation.getCreatedAt())
                .build();

        ReflectionTestUtils.setField(recommendationMapper, "skillOfferMapper", skillOfferMapper);
    }

    @Test
    void testCreate_whenValidInput_thenReturnsCreatedRecommendation() {
        when(skillRepository.existsById(SKILL_ID)).thenReturn(true);
        when(recommendationRepository.create(AUTHOR_ID, RECEIVER_ID, inputDto.getContent()))
                .thenReturn(EXPECTED_DTO_ID);
        when(skillOfferRepository.create(SKILL_ID, EXPECTED_DTO_ID))
                .thenReturn(SKILL_OFFER_ID);
        when(recommendationRepository.findById(EXPECTED_DTO_ID))
                .thenReturn(Optional.of(recommendation));
        doReturn(expectedDto).when(recommendationMapper).toDto(recommendation);

        RecommendationDto result = recommendationService.create(inputDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(recommendationRepository).create(AUTHOR_ID, RECEIVER_ID, inputDto.getContent());
        verify(skillOfferRepository).create(SKILL_ID, EXPECTED_DTO_ID);
        verify(skillRepository).existsById(SKILL_ID);
    }

    @Test
    void testCreate_whenSingleSkillNotExists_thenThrowsDataValidationException() {
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.empty());
        when(skillRepository.existsById(SKILL_ID)).thenReturn(false);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(inputDto));

        assertThat(ex.getMessage())
                .isEqualTo(String.format("Skill with id %d does not exist in the system", SKILL_ID));
    }

    @Test
    void testCreate_whenMultipleSkillsNotExist_thenThrowsDataValidationException() {
        SkillOfferDto dto1 = SkillOfferDto.builder().skillId(5L).build();
        SkillOfferDto dto2 = SkillOfferDto.builder().skillId(7L).build();
        inputDto.setSkillOffers(List.of(dto1, dto2));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.empty());
        when(skillRepository.existsById(5L)).thenReturn(false);
        when(skillRepository.existsById(7L)).thenReturn(false);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(inputDto));

        assertThat(ex.getMessage())
                .isEqualTo("The following skills do not exist in the system: 5, 7");
    }

    @Test
    void testCreate_whenDuplicateSkills_thenThrowsDataValidationException() {
        inputDto.setSkillOffers(List.of(skillOfferDto, skillOfferDto));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.empty());
        when(skillRepository.existsById(SKILL_ID)).thenReturn(true);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(inputDto));

        assertThat(ex.getMessage())
                .isEqualTo("There are duplicate skills in the recommendation");
    }

    @Test
    void testCreate_whenRecommendationTooFrequent_thenThrowsDataValidationException() {
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.of(recommendation));

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(inputDto));

        assertThat(ex.getMessage())
                .isEqualTo("You cannot give a recommendation to the same user more than once every 6 months");
    }

    @Test
    void testUpdate_whenValidInput_thenReturnsUpdatedRecommendation() {
        inputDto.setId(EXPECTED_DTO_ID);
        inputDto.setContent("Helpful");

        when(skillRepository.existsById(SKILL_ID)).thenReturn(true);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.empty());
        doNothing().when(skillOfferRepository).deleteAllByRecommendationId(EXPECTED_DTO_ID);
        when(skillOfferRepository.create(SKILL_ID, EXPECTED_DTO_ID)).thenReturn(SKILL_OFFER_ID);
        when(recommendationRepository.findById(EXPECTED_DTO_ID)).thenReturn(Optional.of(recommendation));
        doReturn(expectedDto).when(recommendationMapper).toDto(recommendation);

        RecommendationDto result = recommendationService.update(inputDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(recommendationRepository).update(AUTHOR_ID, RECEIVER_ID, "Helpful");
        verify(skillOfferRepository).deleteAllByRecommendationId(EXPECTED_DTO_ID);
        verify(skillOfferRepository).create(SKILL_ID, EXPECTED_DTO_ID);
        verify(skillRepository).existsById(SKILL_ID);
    }

    @Test
    void  testDelete_whenValidId_thenRemovesRecommendationAndOffers() {
        long idToDelete = 42L;
        recommendationService.delete(idToDelete);
        InOrder inOrder = inOrder(skillOfferRepository, recommendationRepository);
        inOrder.verify(skillOfferRepository).deleteAllByRecommendationId(idToDelete);
        inOrder.verify(recommendationRepository).deleteById(idToDelete);
    }

    @Test
    void testGetAllUserRecommendations_whenRecommendationsExist_thenReturnsMappedDtos() {
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> recommendationsPage = new PageImpl<>(recommendations);
        List<RecommendationDto> recommendationDtos = recommendationMapper.toDtoList(recommendations);

        PageRequest expectedPageRequest = PageRequest.of(0, Integer.MAX_VALUE);
        when(recommendationRepository.findAllByReceiverId(RECEIVER_ID, expectedPageRequest))
                .thenReturn(recommendationsPage);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(RECEIVER_ID);
        assertThat(result)
                .hasSize(recommendationDtos.size())
                .isEqualTo(recommendationDtos);
        verify(recommendationRepository).findAllByReceiverId(eq(RECEIVER_ID), any(PageRequest.class));
    }

    @Test
    void testGetAllUserRecommendations_whenNoRecommendations_thenReturnsEmptyList() {
        when(recommendationRepository.findAllByReceiverId(eq(RECEIVER_ID), any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(RECEIVER_ID);
        assertThat(result).hasSize(0);
    }

    @Test
    void testGetAllGivenRecommendations_whenRecommendationsExist_thenReturnsMappedDtos() {
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> recommendationsPage = new PageImpl<>(recommendations);
        List<RecommendationDto> recommendationDtos = recommendationMapper.toDtoList(recommendations);

        PageRequest expectedPageRequest = PageRequest.of(0, Integer.MAX_VALUE);
        when(recommendationRepository.findAllByAuthorId(AUTHOR_ID, expectedPageRequest))
                .thenReturn(recommendationsPage);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(AUTHOR_ID);
        assertThat(result)
                .hasSize(recommendationDtos.size())
                .isEqualTo(recommendationDtos);
        verify(recommendationRepository).findAllByAuthorId(eq(AUTHOR_ID), any(PageRequest.class));
    }

    @Test
    void testGetAllGivenRecommendations_whenNoRecommendations_thenReturnsEmptyList()  {
        when(recommendationRepository.findAllByAuthorId(eq(AUTHOR_ID), any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(AUTHOR_ID);
        assertThat(result).hasSize(0);
    }
}