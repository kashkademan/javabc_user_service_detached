package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private static final int PAGE = 1;
    private static final Pageable PAGEABLE = PageRequest.of(1, 100, Sort.by("updated_at").descending());
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;

    @Spy
    private RecommendationMapperImpl recommendationMapper;
    @Spy
    private SkillOfferMapperImpl skillOfferMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testValidationAfterSixMonth() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "", List.of(), NOW);
        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.authorId(),
                        recommendationDto.receiverId()
                ))
                .thenReturn(Optional.of(new Recommendation(1, "1", null, null, null, null, NOW.minusMonths(3), null)));
        assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testValidationNonExistentSkills() {
        RecommendationDto recommendationDto = new RecommendationDto(
                1L, 1L, 1L, "", List.of(new SkillOfferDto(1L, 1L, 1L)), NOW);

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateCreates() {
        RecommendationDto testDto = new RecommendationDto(1L, 1L, 1L, "1", List.of(), NOW);
        when(recommendationRepository.create(1L, 1L, "1")).thenReturn(1L);
        Recommendation recommendation = new Recommendation(1L, "1", null, null, List.of(), null, NOW, null);
        when(recommendationRepository.findById(testDto.id())).thenReturn(Optional.of(recommendation));
        assertEquals(testDto.id(), recommendationService.create(testDto).id());
        assertEquals(testDto.skillOffers(), recommendationService.create(testDto).skillOffers());
        assertEquals(testDto.content(), recommendationService.create(testDto).content());
        assertEquals(testDto.createdAt(), recommendationService.create(testDto).createdAt());
    }

    @Test
    public void testNonExistentId() {
        long id = 1;
        when(recommendationRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> recommendationService.delete(1));
    }

    @Test
    public void testDeleteDeletes() {
        long id = 1;
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(new Recommendation()));
        recommendationService.delete(1);
        verify(recommendationRepository).deleteById(id);
    }

    @Test
    public void testUpdateUpdates() {
        RecommendationDto testDto = new RecommendationDto(1L, 1L, 1L, "1", List.of(), NOW);
        Recommendation recommendation = new Recommendation(1, "1", null, null, List.of(), null, NOW, null);
        when(recommendationRepository.findById(testDto.id())).thenReturn(Optional.of(recommendation));
        assertEquals(testDto.id(), recommendationService.update(testDto).id());
        assertEquals(testDto.skillOffers(), recommendationService.update(testDto).skillOffers());
        assertEquals(testDto.content(), recommendationService.update(testDto).content());
        assertEquals(testDto.createdAt(), recommendationService.update(testDto).createdAt());
    }

    @Test
    public void testGetAllUserRecommendations() {
        long id = 1;
        Recommendation recommendation = new Recommendation(1, "1", null, null, List.of(), null, NOW, null);
        List<Recommendation> recommendationList = List.of(recommendation, recommendation);
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<Recommendation> page = new PageImpl<>(recommendationList, pageRequest, recommendationList.size());
        when(recommendationRepository.findAllByReceiverId(id, PAGEABLE)).thenReturn(page);
        assertEquals(recommendationList.size(), recommendationService.getAllUserRecommendations(id, PAGE).size());
    }

    @Test
    public void testGetAllGivenRecommendations() {
        long id = 1;
        Recommendation recommendation = new Recommendation(
                1, "1", null, null, List.of(), null, LocalDateTime.now(), null);

        List<Recommendation> recommendationList = List.of(recommendation, recommendation);
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<Recommendation> page = new PageImpl<>(recommendationList, pageRequest, recommendationList.size());
        when(recommendationRepository.findAllByAuthorId(id, PAGEABLE)).thenReturn(page);
        assertEquals(recommendationList.stream()
                        .map(recommendationMapper::toDto).toList(),
                recommendationService.getAllGivenRecommendations(id, PAGE));
    }
}
