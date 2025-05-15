package school.faang.user_service.entity.recommendation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recommendation_request")
public class RecommendationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "message", nullable = false, length = 4096)
    private String message;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private RequestStatus status;

    @Column(name = "rejection_reason", length = 4096)
    private String rejectionReason;

    @OneToOne
    @JoinColumn(name = "recommendation_id")
    private Recommendation recommendation;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private final List<SkillRequest> skills = new ArrayList<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addSkillRequest(SkillRequest skillRequest) {
        skills.add(skillRequest);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RecommendationRequest{");
        sb.append("id=").append(id);
        sb.append(", message='").append(message).append('\'');
        sb.append(", status=");
        if (status == null) {
            sb.append("null");
        } else {
            sb.append(status);
        }
        sb.append(", rejectionReason='").append(rejectionReason).append('\'');
        if (requester != null) {
            sb.append(", requester='[id=%d, userName=%s]".formatted(requester.getId(), requester.getUsername()))
                    .append('\'');
        } else {
            sb.append(", requester=[null]");
        }
        if (receiver != null) {
            sb.append(", receiver='[id=%d, userName=%s]".formatted(receiver.getId(), receiver.getUsername()))
                    .append('\'');
        } else {
            sb.append(", receiver=[null]");
        }
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }
}