package school.faang.user_service.service.mentorship;

import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public abstract class DataForTests {
    protected static final LocalDateTime FIXED_LOCAL_DATE_TIME = LocalDateTime.now();
    protected static final long FIXED_MENTORSHIP_REQUEST_ID = 1L;
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
                            arg.status(),
                            arg.menteeId(),
                            arg.mentorId(),
                            arg.createdAt())
            ));

    protected MentorshipRequest mentReqA12 = mentorshipRequests.get(
            new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_1, MENTOR_ID_2, FIXED_LOCAL_DATE_TIME));
    protected MentorshipRequest mentReqP23 = mentorshipRequests.get(
            new ArgsMentorshipRequest(RequestStatus.PENDING, MENTEE_ID_2, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME));
    protected MentorshipRequest mentReqP43 = mentorshipRequests.get(
            new ArgsMentorshipRequest(RequestStatus.PENDING, MENTEE_ID_4, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME));
    protected MentorshipRequest mentReqR13 = mentorshipRequests.get(
            new ArgsMentorshipRequest(RequestStatus.REJECTED, MENTEE_ID_1, MENTOR_ID_3, FIXED_LOCAL_DATE_TIME));
    protected MentorshipRequest mentReqR14 = mentorshipRequests.get(
            new ArgsMentorshipRequest(RequestStatus.REJECTED, MENTEE_ID_1, MENTOR_ID_4, FIXED_LOCAL_DATE_TIME));
    protected MentorshipRequest mentReqA67 = mentorshipRequests.get(
            new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_6, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME));
    protected MentorshipRequest mentReqA87 = mentorshipRequests.get(
            new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_8, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME));
    protected MentorshipRequest mentReqA97 = mentorshipRequests.get(
            new ArgsMentorshipRequest(RequestStatus.ACCEPTED, MENTEE_ID_9, MENTOR_ID_7, FIXED_LOCAL_DATE_TIME));

    protected List<MentorshipRequest> mentorshipRequestAll = List.copyOf(mentorshipRequests.values());

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

        MentorshipRequest mentorshipRequestEntity = new MentorshipRequest();
        mentorshipRequestEntity.setId(FIXED_MENTORSHIP_REQUEST_ID);
        mentorshipRequestEntity.setStatus(status);
        mentorshipRequestEntity.setCreatedAt(createdAt);
        mentorshipRequestEntity.setRequester(userRequester);
        mentorshipRequestEntity.setReceiver(userReceiver);

        return mentorshipRequestEntity;
    }
}