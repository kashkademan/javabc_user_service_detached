package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DataForTests {
    public static final LocalDateTime FIXED_LOCAL_DATE_TIME = LocalDateTime.now();
    protected static final long MENTEE_ID_1 = 1L;
    protected static final long MENTEE_ID_2 = 2L;
    protected static final long MENTEE_ID_4 = 4L;
    protected static final long MENTEE_ID_6 = 6L;
    protected static final long MENTEE_ID_8 = 8L;
    protected static final long MENTEE_ID_9 = 9L;
    protected static final Long MENTEE_ID_NULL = null;
    protected static final long MENTOR_ID_2 = 2L;
    protected static final long MENTOR_ID_3 = 3L;
    protected static final long MENTOR_ID_4 = 4L;
    protected static final long MENTOR_ID_7 = 7L;
    protected static final long MENTOR_ID_9 = 9L;
    protected static final Long MENTOR_ID_NULL = null;
    protected static final RequestStatus REQUEST_STATUS_NULL = null;
    protected List<MentorshipRequest> mentorshipRequestAll;
    protected MentorshipRequest mentReqA12;
    protected MentorshipRequest mentReqR23;
    protected MentorshipRequest mentReqP43;
    protected MentorshipRequest mentReqR13;
    protected MentorshipRequest mentReqR14;
    protected MentorshipRequest mentReqA67;
    protected MentorshipRequest mentReqA87;
    protected MentorshipRequest mentReqA97;

    @BeforeAll
    void configureGlobally() {

        List<ArgsMentorshipRequest> args = List.of(
                new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_1, MENTOR_ID_2, FIXED_LOCAL_DATE_TIME),
                new ArgsMentorshipRequest(RequestStatus.PENDING, MENTEE_ID_2, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME),
                new ArgsMentorshipRequest(RequestStatus.PENDING, MENTEE_ID_4, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME),
                new ArgsMentorshipRequest(RequestStatus.REJECTED, MENTEE_ID_1, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME),
                new ArgsMentorshipRequest(RequestStatus.REJECTED, MENTEE_ID_1, MENTOR_ID_4, FIXED_LOCAL_DATE_TIME),
                new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_6, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME),
                new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_8, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME),
                new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_9, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME)
        );

        Map<ArgsMentorshipRequest, MentorshipRequest> mentorshipRequests = args.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        arg -> createMentorshipRequest(
                                arg.status,
                                arg.menteeId,
                                arg.mentorId,
                                arg.createdAt),
                        (v1, v2) -> v1
                ));

        mentReqA12 = mentorshipRequests.get(
                new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_1, MENTOR_ID_2, FIXED_LOCAL_DATE_TIME));
        mentReqR23 = mentorshipRequests.get(
                new ArgsMentorshipRequest(RequestStatus.PENDING, MENTEE_ID_2, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME));
        mentReqP43 = mentorshipRequests.get(
                new ArgsMentorshipRequest(RequestStatus.PENDING, MENTEE_ID_4, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME));
        mentReqR13 = mentorshipRequests.get(
                new ArgsMentorshipRequest(RequestStatus.REJECTED, MENTEE_ID_1, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME));
        mentReqR14 = mentorshipRequests.get(
                new ArgsMentorshipRequest(RequestStatus.REJECTED, MENTEE_ID_1, MENTOR_ID_4, FIXED_LOCAL_DATE_TIME));
        mentReqA67 = mentorshipRequests.get(
                new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_6, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME));
        mentReqA87 = mentorshipRequests.get(
                new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_8, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME));
        mentReqA97 = mentorshipRequests.get(
                new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_9, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME));

        mentorshipRequestAll = List.copyOf(mentorshipRequests.values());
    }

    protected User generateUser(Long userId) {
        return User
                .builder()
                .id(userId)
                .build();
    }

    protected MentorshipRequest createMentorshipRequest(RequestStatus status,
                                                        long menteeId,
                                                        long mentorId,
                                                        LocalDateTime createdAt) {
        User userReceiver = generateUser(mentorId);
        User userRequester = generateUser(menteeId);
        long mentorshipRequestId = 1L;

        MentorshipRequest mentorshipRequestEntity = new MentorshipRequest();
        mentorshipRequestEntity.setId(mentorshipRequestId);
        mentorshipRequestEntity.setStatus(status);
        mentorshipRequestEntity.setCreatedAt(createdAt);
        mentorshipRequestEntity.setRequester(userRequester);
        mentorshipRequestEntity.setReceiver(userReceiver);

        return mentorshipRequestEntity;
    }

    record ArgsMentorshipRequest(
            RequestStatus status,
            long menteeId,
            long mentorId,
            LocalDateTime createdAt
    ) {
    }
}