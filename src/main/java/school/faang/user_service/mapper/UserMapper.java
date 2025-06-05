package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.Person;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(UserDto userDto);

    List<UserDto> mapListOfUsers(List<User> subscriptions);

    @Mapping(target = "username", expression = "java(person.getFirstName() + \" \" + person.getLastName())")
    @Mapping(target = "aboutMe", expression = "java(buildAboutMe(person))")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "experience", constant = "0")
    @Mapping(target = "country", ignore = true)
    User personToUser(Person person);

    default String buildAboutMe(Person person) {
        return Stream.of(
                        person.getState(),
                        person.getFaculty(),
                        person.getYearOfStudy(),
                        person.getMajor(),
                        person.getEmployer()
                )
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(", "));
    }
}