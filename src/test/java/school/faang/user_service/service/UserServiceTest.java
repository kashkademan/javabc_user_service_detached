package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void getUserById() {
        long id = 1L;
        User user = User.builder()
                .id(id)
                .build();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(id);

        assertNotNull(result);
        assertEquals(id,result.id());
    }

    @Test
    void getUserByIdException(){
        long id = -1L;
        Mockito.when(userRepository.findById(id))
                .thenThrow(new IllegalArgumentException("The Requester with id =" + id + " does not exist"));

        assertThrows(IllegalArgumentException.class, ()->userRepository.findById(id));
    }
}