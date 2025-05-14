package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    // Тут пустой список стратегий, может надо каждую стратегию подгрузить отдельно через мок, но как я могу указать InjectMocks если у меня уже есть ниже?
    @Spy
    private List<RecommendationRequestFilterStrategy> recommendationRequestFilters;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @Test
    public void testCreateThrowsRequesterIsNotFound() {
        long requesterId = 1L;

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(requesterId);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(dto));
    }

    @Test
    public void testCreateThrowsReceiverIsNotFound() {
        long requesterId = 1L;
        long receiverId = 2L;

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(dto));
    }

    @Test
    public void testCreateThrowsRequestHasAlreadyBeenUpdated() {
        long requesterId = 1L;
        long receiverId = 2L;
        LocalDateTime date = LocalDateTime.now().minus(Period.ofMonths(1));

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);
        dto.setUpdatedAt(date);

        RecommendationRequest entity = recommendationRequestMapper.toEntity(dto);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.of(new User()));

        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(entity));

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(dto));
    }

    @Test
    public void testCreateThrowsNotAllRequiredSkillsExist() {
        long requesterId = 1L;
        long receiverId = 2L;
        LocalDateTime date = LocalDateTime.now().minus(Period.ofMonths(10));

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);
        dto.setUpdatedAt(date);
        dto.setSkillIds(List.of(1L));

        RecommendationRequest entity = recommendationRequestMapper.toEntity(dto);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.of(new User()));
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(entity));
        when(skillRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.create(dto));
    }

    @Test
    public void testCreateReturnsRecommendationRequestDto() {
        long requesterId = 1L;
        long receiverId = 2L;
        LocalDateTime date = LocalDateTime.now().minus(Period.ofMonths(10));
        long skillId = 1L;
        long skillRequestId = 1L;
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setId(skillRequestId);
        Skill skill = new Skill();
        skill.setId(skillId);
        long entityId = 1L;

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);
        dto.setUpdatedAt(date);
        dto.setSkillIds(List.of(skillId));

        RecommendationRequestDto expectedDto = new RecommendationRequestDto();
        expectedDto.setRequesterId(requesterId);
        expectedDto.setReceiverId(receiverId);
        expectedDto.setCreatedAt(date);


        RecommendationRequest entity = recommendationRequestMapper.toEntity(dto);
        entity.setSkills(List.of(skillRequest));
        entity.setId(entityId);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.of(new User()));
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(entity));
        when(skillRepository.existsById(1L))
                .thenReturn(true);
        when(skillRepository.findById(1L))
                .thenReturn(Optional.of(skill));
        when(skillRequestRepository.create(entity.getId(), skillRequest.getId()))
                .thenReturn(skillRequest);
        when(recommendationRequestRepository.save(entity))
                .thenReturn(entity);

        assertEquals(recommendationRequestMapper.toDto(entity), recommendationRequestService.create(dto));
    }

//    @Test
//    public void testGetRequestsEmptyFilter() {
//        long recommendationRequestId1 = 1L;
//        long recommendationRequestId2 = 2L;
//
//        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
//        recommendationRequest1.setId(recommendationRequestId1);
//        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
//        recommendationRequest2.setId(recommendationRequestId2);
//
//        RecommendationRequestDto entity1 = new RecommendationRequestDto();
//        entity1.setId(recommendationRequestId1);
//        RecommendationRequestDto entity2 = new RecommendationRequestDto();
//        entity2.setId(recommendationRequestId2);
//
//        List<RecommendationRequestDto> expectedListOfDtos = List.of(entity1, entity2);
//
//        RequestFilterDto requestFilterDto = new RequestFilterDto();
//        requestFilterDto.setRequesterId(null);
//        requestFilterDto.setReceiverId(null);
//        requestFilterDto.setStatus(null);
//        requestFilterDto.setSkillId(null);
//        requestFilterDto.setCreatedAt(null);
//
//        when(recommendationRequestRepository.findAll())
//                .thenReturn(List.of(recommendationRequest1, recommendationRequest2));
//        recommendationRequestFilters.forEach(filter ->
//                when(filter.isApplicable(requestFilterDto))
//                        .thenReturn(false));
//
//        assertEquals(expectedListOfDtos, recommendationRequestService.getRequests(requestFilterDto));
//    }

    @Test
    public void testGetRequestThrowsNoSuchElementException() {
        long id = 1L;

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.getRequest(id));
    }

    @Test
    public void testGetRequestReturnsRecommendationRequestDto() {
        long id = 1L;
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(id);
        recommendationRequest.setSkills(List.of()); // Стоит ли добавлять в логику проверку что бы тест не зависел от того пуст лист или нет?

        RecommendationRequestDto expectedDto = recommendationRequestMapper.toDto(recommendationRequest);

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(recommendationRequest));

        assertEquals(expectedDto, recommendationRequestService.getRequest(id));
    }

    @Test
    public void testRejectResultThrowsRequestDoesntExist() {
        long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    public void testRejectResultThrowsUnableToReject() {
        long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(id);
        recommendationRequest.setStatus(RequestStatus.REJECTED);

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    public void testRejectResultReturnsRecommendationRequestDto() {
        long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Reason");
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(id);
        recommendationRequest.setSkills(List.of());
        recommendationRequest.setStatus(RequestStatus.PENDING);
        RecommendationRequestDto recommendationRequestDto = recommendationRequestMapper.toDto(recommendationRequest);
        recommendationRequestDto.setStatus(RequestStatus.REJECTED);

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(recommendationRequest));

        assertEquals(recommendationRequestDto, recommendationRequestService.rejectRequest(id, rejectionDto));
    }
}
