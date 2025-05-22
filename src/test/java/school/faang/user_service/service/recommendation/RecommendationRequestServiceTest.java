package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private RecommendationRequestFilterStrategy receiverIdFilter;
    @Mock
    private RecommendationRequestFilterStrategy requesterIdFilter;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    private RecommendationRequestServiceImpl recommendationRequestService;

    private long requesterId;
    private long receiverId;
    private RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    public void setUp() {
        recommendationRequestService = new RecommendationRequestServiceImpl(
                userRepository,
                skillRepository,
                skillRequestRepository,
                recommendationRequestRepository,
                List.of(
                        receiverIdFilter,
                        requesterIdFilter
                ),
                recommendationRequestMapper
        );

        requesterId = 1L;
        receiverId = 2L;

        recommendationRequestDto = new RecommendationRequestDto();
    }

    @Test
    public void testCreate_ThrowsRequesterIsNotFound() {
        recommendationRequestDto.setRequesterId(requesterId);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestDto));

        assertEquals("Requester with id %s was not found".formatted(requesterId), exception.getMessage());
    }

    @Test
    public void testCreate_ThrowsReceiverIsNotFound() {
        recommendationRequestDto.setRequesterId(requesterId);
        recommendationRequestDto.setReceiverId(receiverId);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestDto));

        assertEquals("Receiver with id %s was not found".formatted(receiverId), exception.getMessage());
    }

    @Test
    public void testCreate_ThrowsRequestHasAlreadyBeenUpdated() {
        LocalDateTime date = LocalDateTime.now().minus(Period.ofMonths(1));

        recommendationRequestDto.setRequesterId(requesterId);
        recommendationRequestDto.setReceiverId(receiverId);
        recommendationRequestDto.setUpdatedAt(date);

        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.of(new User()));

        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(entity));

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    public void testCreate_ThrowsNotAllRequiredSkillsExist() {
        LocalDateTime date = LocalDateTime.now().minus(Period.ofMonths(10));

        recommendationRequestDto.setRequesterId(requesterId);
        recommendationRequestDto.setReceiverId(receiverId);

        recommendationRequestDto.setUpdatedAt(date);
        recommendationRequestDto.setSkillIds(List.of(1L));

        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);

        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.of(new User()));
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(entity));
        when(skillRepository.existsById(1L))
                .thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestDto));

        assertEquals("Not all required skills exist in data base", exception.getMessage());
    }

    @Test
    public void testCreate_ReturnsRecommendationRequestDto() {
        LocalDateTime date = LocalDateTime.now().minus(Period.ofMonths(10));
        long skillId = 1L;
        long skillRequestId = 1L;
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setId(skillRequestId);
        Skill skill = new Skill();
        skill.setId(skillId);
        long entityId = 1L;

        recommendationRequestDto.setRequesterId(requesterId);
        recommendationRequestDto.setReceiverId(receiverId);
        recommendationRequestDto.setUpdatedAt(date);
        recommendationRequestDto.setSkillIds(List.of(skillId));

        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);
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

        assertEquals(recommendationRequestMapper.toDto(entity), recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    public void testGetRequests_EmptyFilter() {
        long recommendationRequestId1 = 1L;
        long recommendationRequestId2 = 2L;

        RecommendationRequest entity1 = new RecommendationRequest();
        entity1.setId(recommendationRequestId1);
        RecommendationRequest entity2 = new RecommendationRequest();
        entity2.setId(recommendationRequestId2);

        RecommendationRequestDto dto1 = new RecommendationRequestDto();
        dto1.setId(recommendationRequestId1);
        dto1.setSkillIds(new ArrayList<>());
        RecommendationRequestDto dto2 = new RecommendationRequestDto();
        dto2.setId(recommendationRequestId2);
        dto2.setSkillIds(new ArrayList<>());

        List<RecommendationRequestDto> listOfExpectedDtos = List.of(dto1, dto2);

        RequestFilterDto requestFilterDto = new RequestFilterDto();

        when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        assertEquals(listOfExpectedDtos, recommendationRequestService.getRequests(requestFilterDto));
    }

    @Test
    public void testGetRequests_FilterOneParam() {
        long recommendationRequestId1 = 1L;
        long recommendationRequestId2 = 2L;

        User requester = User.builder().id(10L).build();
        User receiver = User.builder().id(20L).build();

        RecommendationRequest entity1 = new RecommendationRequest();
        entity1.setId(recommendationRequestId1);
        entity1.setRequester(requester);
        entity1.setReceiver(receiver);
        RecommendationRequest entity2 = new RecommendationRequest();
        entity2.setId(recommendationRequestId2);

        RecommendationRequestDto dto1 = recommendationRequestMapper.toDto(entity1);

        List<RecommendationRequestDto> listOfExpectedDtos = List.of(dto1);

        RequestFilterDto requestFilterDto = new RequestFilterDto();

        when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1));

        when(requesterIdFilter.isApplicable(any())).thenReturn(true);
        when(receiverIdFilter.isApplicable(any())).thenReturn(false);

        when(requesterIdFilter.apply(any(), any()))
                .thenAnswer((Answer<Stream<RecommendationRequest>>) invocations -> {
                    Stream<RecommendationRequest> stream = invocations.getArgument(0);

                    return stream.filter(recommendationRequest ->
                            recommendationRequest.getRequester().getId().equals(requester.getId()));
                });

        assertEquals(listOfExpectedDtos, recommendationRequestService.getRequests(requestFilterDto));
    }

    @Test
    public void testGetRequests_PassSeparateFilters_ReturnOneDto() {
        User requester1 = User.builder().id(10L).build();
        User requester2 = User.builder().id(20L).build();
        User receiver1 = User.builder().id(30L).build();
        User receiver2 = User.builder().id(40L).build();

        long recommendationRequestId1 = 1L;
        long recommendationRequestId2 = 2L;
        long recommendationRequestId3 = 3L;

        RecommendationRequest entity1 = new RecommendationRequest();
        entity1.setId(recommendationRequestId1);
        entity1.setRequester(requester1);
        entity1.setReceiver(receiver1);
        RecommendationRequest entity2 = new RecommendationRequest();
        entity2.setId(recommendationRequestId2);
        entity2.setRequester(requester2);
        entity2.setReceiver(receiver2);
        RecommendationRequest entity3 = new RecommendationRequest();
        entity3.setId(recommendationRequestId3);
        entity3.setRequester(requester1);
        entity3.setReceiver(receiver2);

        RequestFilterDto requestFilterDto = new RequestFilterDto();

        when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2, entity3));

        when(requesterIdFilter.isApplicable(any())).thenReturn(true);
        when(receiverIdFilter.isApplicable(any())).thenReturn(true);

        when(requesterIdFilter.apply(any(), any()))
                .thenAnswer((Answer<Stream<RecommendationRequest>>) invocations -> {
                    Stream<RecommendationRequest> stream = invocations.getArgument(0);

                    return stream.filter(recommendationRequest ->
                            recommendationRequest.getRequester().getId().equals(requester1.getId()));
                });

        when(receiverIdFilter.apply(any(), any()))
                .thenAnswer((Answer<Stream<RecommendationRequest>>) invocations -> {
                    Stream<RecommendationRequest> stream = invocations.getArgument(0);

                    return stream.filter(recommendationRequest ->
                            recommendationRequest.getReceiver().getId().equals(receiver2.getId()));
                });

        assertEquals(List.of(recommendationRequestMapper.toDto(entity3)), recommendationRequestService.getRequests(requestFilterDto));
    }

    @Test
    public void testGetRequestThrowsNoSuchElementException() {
        long id = 1L;

        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.getRequest(id));

        assertEquals("Recommendation request with id %s doesn't exist".formatted(id), exception.getMessage());
    }

    @Test
    public void testGetRequestReturnsRecommendationRequestDto() {
        long id = 1L;
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(id);
        recommendationRequest.setSkills(List.of());

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

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));

        assertEquals("Recommendation request with id %s doesn't exist".formatted(id), exception.getMessage());
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

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));

        assertEquals("Unable to reject request due to %s status".formatted(RequestStatus.REJECTED), exception.getMessage());
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
