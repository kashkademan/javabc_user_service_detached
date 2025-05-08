package school.faang.user_service.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

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
    private List<User> followers;

    @ManyToMany(mappedBy = "followers")
    @ToString.Exclude
    private List<User> followees;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    private List<Event> ownedEvents;

    @ManyToMany(mappedBy = "mentors")
    @ToString.Exclude
    private List<User> mentees;

    @ManyToMany
    @JoinTable(name = "mentorship",
            joinColumns = @JoinColumn(name = "mentee_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id"))
    @ToString.Exclude
    private List<User> mentors;

    @OneToMany(mappedBy = "receiver")
    @ToString.Exclude
    private List<MentorshipRequest> receivedMentorshipRequests;

    @OneToMany(mappedBy = "requester")
    @ToString.Exclude
    private List<MentorshipRequest> sentMentorshipRequests;

    @OneToMany(mappedBy = "inviter")
    @ToString.Exclude
    private List<GoalInvitation> sentGoalInvitations;

    @OneToMany(mappedBy = "invited")
    @ToString.Exclude
    private List<GoalInvitation> receivedGoalInvitations;

    @OneToMany(mappedBy = "mentor")
    @ToString.Exclude
    private List<Goal> setGoals;

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private List<Goal> goals;

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private List<Skill> skills;

    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @ToString.Exclude
    private List<Event> participatedEvents;

    @OneToMany(mappedBy = "author")
    @ToString.Exclude
    private List<Recommendation> recommendationsGiven;

    @OneToMany(mappedBy = "receiver")
    @ToString.Exclude
    private List<Recommendation> recommendationsReceived;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Contact> contacts;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Rating> ratings;

    @Embedded
    @AttributeOverride(name = "fileId", column = @Column(name = "profile_pic_file_id"))
    @AttributeOverride(name = "smallFileId", column = @Column(name = "profile_pic_small_file_id"))
    private UserProfilePic userProfilePic;

    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private ContactPreference contactPreference;

    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private Premium premium;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Education> education;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Career> career;

    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private WorkSchedule workSchedule;
}