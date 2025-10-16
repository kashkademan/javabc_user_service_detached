package school.faang.user_service.entity.skill;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;

@Entity
@Getter
@Setter
@Table(name = "skill_offer")
public class SkillOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "offered_user_id")
    private User offeredUser;
}