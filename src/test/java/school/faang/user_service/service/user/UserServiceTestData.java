package school.faang.user_service.service.user;

import org.junit.jupiter.params.provider.Arguments;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class UserServiceTestData {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    public UserDto getViewDto() {
        return new UserDto(
                1L,
                "JohnDoe",
                "johndoe@example.com",
                "1234567890",
                "About John Doe"
        );
    }

    public UserDto getViewDto(User user) {
        return mapper.toUserDto(user);
    }

    public Country getCountry(long id, String name) {
        return new Country(id, name, new ArrayList<>());
    }

    public UserCreateDto getCreateDto(String name, long countryId) {
        return new UserCreateDto(
                name,
                "example@gmail.com",
                UUID.randomUUID().toString(),
                countryId
        );
    }

    public User getUser(Long id, String name, Country country) {
        var createDto = getCreateDto(name, country.getId());
        var user = mapper.toUser(createDto);
        user.setId(id);
        user.setCountry(country);
        user.setPhone("87477477474");
        user.setAboutMe("About " + name);
        return user;
    }

    public User getUser(Long id, UserCreateDto createDto, Country country) {
        var user = mapper.toUser(createDto);
        user.setId(id);
        user.setCountry(country);
        return user;
    }

    public UserUpdateDto getUpdateDto(User user) {
        return new UserUpdateDto(
                "new " + user.getUsername(),
                "newEmail@gmail.com",
                null,
                null,
                user.getCountry().getId(),
                "New Yourk"
        );
    }

    public static Stream<Arguments> provideParams() {
        var createDto1 = new UserCreateDto(
                "JohnDoe",
                "johndoe@example.com",
                "Mega_str0ng_passwd",
                1L
        );
        var createDto2 = new UserCreateDto(
                "JaneSmith",
                "janesmith@example.com",
                "Mega_str0ng_passwd2",
                1L
        );
        var createDto3 = new UserCreateDto(
                "MichaelJohnson",
                "michaeljohnson@example.com",
                "Mega_str0ng_passwd3",
                1L
        );
        var mapper = new UserMapperImpl();
        var userWithPremium = mapper.toUser(createDto1);
        userWithPremium.setId(1L);
        var userWithPremium2 = mapper.toUser(createDto2);
        userWithPremium2.setId(2L);
        var userWithoutPremium = mapper.toUser(createDto3);
        userWithoutPremium.setId(3L);

        var filter1 = new UserFilterDto(
                null,
                null,
                null,
                null,
                true
        );
        var filter2 = new UserFilterDto(
                "John",
                null,
                null,
                null,
                false
        );

        return Stream.of(
                Arguments.of(filter1,
                        List.of(userWithPremium, userWithPremium2),
                        List.of(mapper.toUserDto(userWithPremium), mapper.toUserDto(userWithPremium2))),
                Arguments.of(filter2,
                        List.of(userWithPremium, userWithPremium2, userWithoutPremium),
                        List.of(mapper.toUserDto(userWithPremium), mapper.toUserDto(userWithoutPremium)))
        );
    }
}
