package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DescriptionFilterTest {
    private final DescriptionFilter descriptionFilter = new DescriptionFilter();

    @Test
    @DisplayName("Description is good")
    public void TestIsApplicableTrue() {
        boolean result = descriptionFilter.isApplicable(new MentorshipFilterDto("Test description", null, null, null));
        assertTrue(result);
    }

    @Test
    @DisplayName("Description is empty")
    public void TestIsApplicableFalseWhenDescriptionIsEmpty() {
        boolean result = descriptionFilter.isApplicable(new MentorshipFilterDto("", null, null, null));
        assertFalse(result);
    }

    @Test
    @DisplayName("Description is blank")
    public void TestIsApplicableFalseWhenDescriptionIsBlank() {
        boolean result = descriptionFilter.isApplicable(new MentorshipFilterDto("    ", null, null, null));
        assertFalse(result);
    }

    @Test
    @DisplayName("Description is null")
    public void TestIsApplicableFalse() {
        boolean result = descriptionFilter.isApplicable(new MentorshipFilterDto(null, null, null, null));
        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<MentorshipRequest> mentorshipRequests = Stream.of(
                MentorshipRequest.builder().description("Test").build(),
                MentorshipRequest.builder().description("Request").build()
        );
        //заменить в коде
//        List<MentorshipRequest> result = descriptionFilter
//                .apply(Stream.of(request), filterDto)
//                .toList();


        Stream<MentorshipRequest> request = descriptionFilter.apply(mentorshipRequests, new MentorshipFilterDto("Test", null,null, null));
        List<MentorshipRequest> requestList = request.toList();

        assertEquals(1,requestList.size());
        assertEquals("Test", requestList.get(0).getDescription());
    }
}
