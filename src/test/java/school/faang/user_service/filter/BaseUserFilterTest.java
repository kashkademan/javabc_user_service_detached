package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;

@ExtendWith(MockitoExtension.class)
abstract class BaseUserFilterTest {

    protected User user1;
    protected User user2;
    protected User user3;
    protected User userNullName;
    protected User userNullPhone;

    @BeforeEach
    void setUpUsers() {
        user1 = User.builder()
                .id(1L)
                .username("User1")
                .phone("123")
                .experience(5)
                .build();
        user2 = User.builder().
                id(2L).username("User2")
                .phone("12345")
                .experience(10)
                .build();
        user3 = User.builder()
                .id(3L)
                .username("User3")
                .phone("456")
                .experience(15)
                .build();
        userNullName = User.builder()
                .id(4L)
                .username(null)
                .phone("999")
                .experience(20)
                .build();
        userNullPhone = User.builder()
                .id(5L)
                .username("userNullPhone")
                .phone(null)
                .experience(20)
                .build();
    }
}
