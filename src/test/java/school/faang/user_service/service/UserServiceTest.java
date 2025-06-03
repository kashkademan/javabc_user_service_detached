package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFullDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserDto;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private CountryService countryService;
    @InjectMocks
    private UserService userService;

    @Test
    void newUserTestIncorrectUsername() {
        UserFullDto dto = createDto("123", "qw@qwer.ru", "22222222222", -1);
        assertThrows(IllegalArgumentException.class, () -> userService.newUser(dto, null));
    }

    @Test
    void newUserTestIncorrectEmail() {
        UserFullDto dto = createDto("qwe", "qwqwerru", "22222222222", 1);

        assertThrows(IllegalArgumentException.class, () -> userService.newUser(dto, null));
    }

    @Test
    void newUserTestIncorrectPhone() {
        UserFullDto dto = createDto("qwe", "qw@qwer.ru", "2", 2);

        assertThrows(IllegalArgumentException.class, () -> userService.newUser(dto, null));
    }

    @Test
    void newUserTestIncorrectExperience() {
        UserFullDto dto = createDto("qwe", "qw@qwer.ru", "22222222222", -1);

        assertThrows(IllegalArgumentException.class, () -> userService.newUser(dto, null));
    }

    @Test
    void newUserTestFilterIsNull() throws IOException {
        Long userId = 1L;
        UserFullDto dto = createDto("qwe", "qw@qwer.ru", "22222222222", 1);
        Country country = new Country();
        String filter = null;
        User user = User.builder()
                .id(userId)
                .build();

        when(countryService.getCountryByID(any())).thenReturn(country);
        when(userMapper.toEntity(any())).thenReturn(user);

        Long result = userService.newUser(dto, filter);

        assertNotNull(result);

        assertEquals(userId, result);
    }

    @Test
    void newUserTestFilterIsNotNull() throws IOException {
        Long userId = 1L;
        UserFullDto dto = createDto("qwe", "qw@qwer.ru", "22222222222", 1);
        Country country = new Country();
        String filter = "hear-someone";
        User user = User.builder()
                .id(userId)
                .build();

        when(countryService.getCountryByID(any())).thenReturn(country);
        when(userMapper.toEntity(any())).thenReturn(user);

        Long result = userService.newUser(dto, filter);

        assertNotNull(result);

        assertEquals(userId, result);
    }
    
   @Test
   void getUserByIdException(){
        long id = -1L;
        Mockito.when(userRepository.findById(id))
                .thenThrow(new IllegalArgumentException("The Requester with id =" + id + " does not exist"));

        assertThrows(IllegalArgumentException.class, ()->userRepository.findById(id));
   }
  
    private UserFullDto createDto(String username, String email, String phone, Integer experience) {
        return UserFullDto.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .experience(experience)
                .build();
    }

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
}