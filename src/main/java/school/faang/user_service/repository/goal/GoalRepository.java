package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;
import java.util.stream.Stream;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query(nativeQuery = true, value = """
            SELECT g.* FROM goal g
            JOIN user_goal ug ON g.id = ug.goal_id
            WHERE ug.user_id = ?1
            """)
    Stream<Goal> findGoalsByUserId(long userId);

    @Query(nativeQuery = true, value = """
            INSERT INTO goal (title, description, parent_goal_id, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW()) RETURNING *
            """)
    Goal create(String title, String description, Long parent);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(ug.goal_id) FROM user_goal ug
            JOIN goal g ON g.id = ug.goal_id
            WHERE ug.user_id = :userId AND g.status = 0
            """)
    int countActiveGoalsPerUser(long userId);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM user_goal WHERE user_id = :userId AND goal_id = :goalId
            """)
    void deleteUserFromGoal(long userId, long goalId);

    @Query(nativeQuery = true, value = """
            WITH RECURSIVE subtasks AS (
            SELECT * FROM goal WHERE id = :goalId
            UNION
            SELECT g.* FROM goal g
            JOIN subtasks st ON st.id = g.parent_goal_id
            )
            SELECT * FROM subtasks WHERE id != :goalId
            """)
    Stream<Goal> findByParent(long goalId);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN user_goal ug ON u.id = ug.user_id
            WHERE ug.goal_id = :goalId
            """)
    List<User> findUsersByGoalId(long goalId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM goal_skill gs WHERE gs.goal_id = ?1")
    void removeSkillsFromGoal(long goalId);

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO goal_skill (goal_id, skill_id) VALUES (?2, ?1)")
    void addSkillToGoal(long skillId, long goalId);

    default Goal getByIdOrThrow(long goalId) {
        return findById(goalId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Goal %d not found", goalId))
        );
    }

    void deleteById(Long goalId);
}
