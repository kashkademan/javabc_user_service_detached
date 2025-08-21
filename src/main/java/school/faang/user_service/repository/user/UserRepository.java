package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.rating_service.rating_aspect.UserIdUsernameProjection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT COUNT(s.id) FROM users u
            JOIN user_skill us ON us.user_id = u.id
            JOIN skill s ON us.skill_id = s.id
            WHERE u.id = ?1 AND s.id IN (?2)
            """)
    int countOwnedSkills(long userId, List<Long> ids);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN user_premium up ON up.user_id = u.id
            WHERE up.end_date > NOW()
            """)
    Stream<User> findPremiumUsers();

    List<User> findByUsernameLike(String username);

    User findByEmailIgnoreCase(String email);

    List<UserIdUsernameProjection> findByIdIn(List<Long> ids);

    Optional<User> findByChatId(Long chatId);

    default User getByIdOrThrow(long userId) {
        return findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", userId)));
    }

    default User getByChatIdOrThrow(long chatId) {
        return findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with chatId %d not found", chatId)));
    }

    @Modifying
    @Query("UPDATE User u SET u.chatId = :chatId WHERE u.id = :userId")
    int updateChatId(Long userId, Long chatId);
}