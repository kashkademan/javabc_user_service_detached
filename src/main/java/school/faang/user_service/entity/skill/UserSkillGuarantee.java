<<<<<<<< HEAD:src/main/java/school/faang/user_service/entity/user/UserSkillGuarantee.java
package school.faang.user_service.entity.user;
========
package school.faang.user_service.entity.skill;
>>>>>>>> remotes/origin/cerberus-master-stream10:src/main/java/school/faang/user_service/entity/skill/UserSkillGuarantee.java

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<<< HEAD:src/main/java/school/faang/user_service/entity/user/UserSkillGuarantee.java
import school.faang.user_service.entity.Skill;
========
import school.faang.user_service.entity.user.User;
>>>>>>>> remotes/origin/cerberus-master-stream10:src/main/java/school/faang/user_service/entity/skill/UserSkillGuarantee.java

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_skill_guarantee")
public class UserSkillGuarantee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "guarantor_id")
    private User guarantor;
}