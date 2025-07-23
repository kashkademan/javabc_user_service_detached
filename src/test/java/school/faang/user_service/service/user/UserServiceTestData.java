package school.faang.user_service.service.user;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class UserServiceTestData {
    public static UserDto toViewDto(User user) {
        if (user == null) {
            return null;
        }
        var id = user.getId();
        var username = user.getUsername();
        var email = user.getEmail();
        var phone = user.getPhone();
        var aboutMe = user.getAboutMe();

        return new UserDto(id, username, email, phone, aboutMe);
    }

    public static Country buildCountry(long id, String name) {
        return new Country(id, name, new ArrayList<>());
    }

    public static UserCreateDto buildCreateDto(String name, long countryId) {
        return new UserCreateDto(
                name,
                "example@gmail.com",
                UUID.randomUUID().toString(),
                countryId
        );
    }

    public static User buildFullUser(Long id, String name, Country country) {
        User.UserBuilder user = User.builder();
        user.id(id);
        user.username(name);
        user.email("example@email.com");
        user.password(UUID.randomUUID().toString());
        user.country(country);
        user.phone("87477477474");
        user.aboutMe("About " + name);
        return user.build();
    }

    public static User buildFullUser(Long id, UserCreateDto createDto, Country country) {
        User.UserBuilder user = User.builder();
        user.id(id);
        user.username(createDto.username());
        user.email(createDto.email());
        user.password(createDto.password());
        user.country(country);
        user.phone("87477477474");
        user.aboutMe("About " + createDto.username());
        return user.build();
    }

    public static User buildLiteUser(Long id, UserCreateDto createDto, Country country) {
        User.UserBuilder user = User.builder();
        user.id(id);
        user.username(createDto.username());
        user.email(createDto.email());
        user.password(createDto.password());
        user.country(country);
        return user.build();
    }
    public static UserUpdateDto buildUpdateDto(User user) {
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
        var kz = buildCountry(1L, "Kazakhstan");
        var userWithPremium = buildFullUser(1L, "JohnDoe", kz);
        var userWithPremium2 = buildFullUser(2L, "JaneSmith", kz);
        var userWithoutPremium = buildFullUser(3L, "MichaelJohnson", kz);
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
                        List.of(userWithPremium, userWithPremium2),
                        List.of(toViewDto(userWithPremium), toViewDto(userWithPremium2))),
                Arguments.of(filter2,
                        List.of(userWithPremium, userWithPremium2, userWithoutPremium),
                        List.of(userWithPremium, userWithoutPremium),
                        List.of(toViewDto(userWithPremium), toViewDto(userWithoutPremium)))
        );
    }
}
