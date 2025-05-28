package school.faang.user_service.model.redis.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;

@RedisHash("eventPromotion")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    @ToString.Exclude
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", updatable = false)
    @ToString.Exclude
    private Event event;

    @Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private PromotionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false, updatable = false)
    @ToString.Exclude
    private Long tariffId;

    private LocalDateTime endDate;
    private Integer countView;
}

