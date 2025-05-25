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
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
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

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private Pageable pageable;
    @Spy
    private RecommendationMapper recommendationMapper;
    @Spy
    private SkillOfferMapper skillOfferMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testValidationAfterSixMonth() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "", List.of(), LocalDateTime.now());
        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.authorId(),
                        recommendationDto.receiverId()
                )).thenReturn(Optional.of(new Recommendation(1, "1", null, null, null, null, LocalDateTime.now().minusMonths(3), null)));
        assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testValidationNonExistentSkills() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "", List.of(new SkillOfferDto(1L, 1L, 1L)), LocalDateTime.now());
        assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateCreates() {
        LocalDateTime now = LocalDateTime.now();
        RecommendationDto testDto = new RecommendationDto(1L, 1L, 1L, "1", List.of(), now);
        when(recommendationRepository.create(1L, 1L, "1")).thenReturn(1L);
        Recommendation recommendation = new Recommendation(1, "1", null, null, List.of(), null, now, null);
        when(recommendationRepository.findById(testDto.id())).thenReturn(Optional.of(recommendation));
        recommendationService.create(testDto);
        verify(recommendationRepository).create(1, 1, "1");
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
        LocalDateTime now = LocalDateTime.now();
        RecommendationDto testDto = new RecommendationDto(1L, 1L, 1L, "1", List.of(), now);
        Recommendation recommendation = new Recommendation(1, "1", null, null, List.of(), null, now, null);
        when(recommendationRepository.findById(testDto.id())).thenReturn(Optional.of(recommendation));
        recommendationService.update(testDto);
        verify(recommendationRepository).update(testDto.authorId(), testDto.receiverId(), testDto.content());
        verify(skillOfferRepository).deleteAllByRecommendationId(testDto.id());
    }

    @Test
    public void testWrongReceiverId() {
        long id = 1;
        when(recommendationRepository.findAllByReceiverId(id, pageable)).thenReturn(Page.empty());
        assertThrows(IllegalArgumentException.class,
                () -> recommendationService.getAllUserRecommendations(id));
    }

    @Test
    public void testGetAllUserRecommendations() {
        long id = 1;
        List<Recommendation> recommendationList = List.of(new Recommendation(), new Recommendation());
        PageRequest pageRequest = PageRequest.of(0, 2); // page = 0, size = 10
        Page<Recommendation> page = new PageImpl<>(recommendationList, pageRequest, recommendationList.size());
        when(recommendationRepository.findAllByReceiverId(id, pageable)).thenReturn(page);

        int lengthOfTheList = recommendationService.getAllUserRecommendations(id).size();
        assertEquals(recommendationList.size(), lengthOfTheList);
    }

    @Test
    public void wrongUserId() {
        long id = 1;
        when(recommendationRepository.findAllByAuthorId(id, pageable)).thenReturn(Page.empty());
        assertThrows(IllegalArgumentException.class,
                () -> recommendationService.getAllGivenRecommendations(id));
    }

    @Test
    public void testGetAllGivenRecommendations() {
        long id = 1;
        List<Recommendation> recommendationList = List.of(new Recommendation(), new Recommendation());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<Recommendation> page = new PageImpl<>(recommendationList, pageRequest, recommendationList.size());
        when(recommendationRepository.findAllByAuthorId(id, pageable)).thenReturn(page);

        List<RecommendationDto> recommendationDtoList = recommendationService.getAllGivenRecommendations(id);
        assertEquals(recommendationList.stream().map(recommendationMapper::toDto).toList(), recommendationDtoList);
    }
}
