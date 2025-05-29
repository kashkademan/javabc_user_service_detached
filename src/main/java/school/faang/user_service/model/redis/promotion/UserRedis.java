package school.faang.user_service.model.redis.promotion;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class UserRedis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 64, nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 32, unique = true)
    private String phone;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "about_me", length = 4096)
    private String aboutMe;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    @ToString.Exclude
    private Country country;

    @Column(name = "city", length = 64)
    private String city;

    @Column(name = "experience")
    private Integer experience;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(name = "subscription",
            joinColumns = @JoinColumn(name = "followee_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
    @ToString.Exclude
    private List<User> followers = new ArrayList<>();

    @ManyToMany(mappedBy = "followers")
    @ToString.Exclude
    private List<User> followees = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    private List<Event> ownedEvents = new ArrayList<>();

    @ManyToMany(mappedBy = "mentors")
    @ToString.Exclude
    private List<User> mentees = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "mentorship",
            joinColumns = @JoinColumn(name = "mentee_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id"))
    @ToString.Exclude
    private List<User> mentors = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @ToString.Exclude
    private List<MentorshipRequest> receivedMentorshipRequests = new ArrayList<>();

    @OneToMany(mappedBy = "requester")
    @ToString.Exclude
    private List<MentorshipRequest> sentMentorshipRequests = new ArrayList<>();

    @OneToMany(mappedBy = "inviter")
    @ToString.Exclude
    private List<GoalInvitation> sentGoalInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "invited")
    @ToString.Exclude
    private List<GoalInvitation> receivedGoalInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "mentor")
    @ToString.Exclude
    private List<Goal> setGoals = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private List<Goal> goals = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private List<Skill> skills = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @ToString.Exclude
    private List<Event> participatedEvents = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    @ToString.Exclude
    private List<Recommendation> recommendationsGiven = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @ToString.Exclude
    private List<Recommendation> recommendationsReceived = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Rating> ratings = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fileId", column = @Column(name = "profile_pic_file_id")),
            @AttributeOverride(name = "smallFileId", column = @Column(name = "profile_pic_small_file_id"))
    })
    @ToString.Exclude
    private UserProfilePic userProfilePic;

    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private ContactPreference contactPreference;

    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private Premium premium;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Education> education = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Career> career = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private WorkSchedule workSchedule;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Promotion> promotions = new ArrayList<>();
}
