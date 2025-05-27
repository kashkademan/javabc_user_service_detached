package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPersonalDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private Long countryId;
    private String city;
    private String pictureFileId;
    private String pictureSmallFileId;
    private Long premiumId;
    private Long workScheduleId;
}
//todo make UserBusinessDto
//    private Long id;
//    private boolean active;
//    private Integer experience;
//    private List<Long> goalIds;
//    private List<Long> skillIds;
//    private List<Long> menteeIds;
//    private List<Long> mentorIds;
//    private List<Long> recommendationGivenIds;
//    private List<Long> recommendationReceivedIds;
//    private List<Long> careerIds;
//    private List<Long> ratingIds;
//    private List<Long> educationIds;

//todo and UserRelationsDto
//    private Long id;
//    private Long followersNumbers;
//    private List<Long> followeeIds;
//    private List<Long> ownedEventIds;
//    private List<Long> receivedMentorshipRequestIds;
//    private List<Long> sentMentorshipRequestIds;
//    private List<Long> sentGoalInvitationIds;
//    private List<Long> receivedGoalInvitationIds;
//    private List<Long> participatedEventIds;
//    private Long contactPreferenceId;
//    private List<Long> contactIds;
