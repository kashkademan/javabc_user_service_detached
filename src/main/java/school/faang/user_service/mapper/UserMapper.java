package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.Person;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(User user);
    User toUser(UserDto userDto);
    List<UserDto> mapListOfUsers(List<User> subscriptions);

    @Mapping(target = "username", expression = "java(person.getFirstName() + \" \" + person.getLastName())")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "aboutMe", expression = "java(buildAboutMe(person))")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "experience", constant = "0")
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followees", ignore = true)
    @Mapping(target = "ownedEvents", ignore = true)
    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "receivedMentorshipRequests", ignore = true)
    @Mapping(target = "sentMentorshipRequests", ignore = true)
    @Mapping(target = "sentGoalInvitations", ignore = true)
    @Mapping(target = "receivedGoalInvitations", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "participatedEvents", ignore = true)
    @Mapping(target = "recommendationsGiven", ignore = true)
    @Mapping(target = "recommendationsReceived", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "userProfilePic", ignore = true)
    @Mapping(target = "contactPreference", ignore = true)
    @Mapping(target = "premium", ignore = true)
    @Mapping(target = "education", ignore = true)
    @Mapping(target = "career", ignore = true)
    @Mapping(target = "workSchedule", ignore = true)
    User personToUser(Person person);

    default String buildAboutMe(Person person) {
        StringBuilder aboutMe = new StringBuilder();

        if (person.getState() != null && !person.getState().trim().isEmpty()) {
            aboutMe.append(person.getState());
        }

        if (person.getFaculty() != null && !person.getFaculty().trim().isEmpty()) {
            if (!aboutMe.isEmpty()) aboutMe.append(", ");
            aboutMe.append(person.getFaculty());
        }

        if (person.getYearOfStudy() != null && !person.getYearOfStudy().trim().isEmpty()) {
            if (!aboutMe.isEmpty()) aboutMe.append(", ");
            aboutMe.append(person.getYearOfStudy());
        }

        if (person.getMajor() != null && !person.getMajor().trim().isEmpty()) {
            if (!aboutMe.isEmpty()) aboutMe.append(", ");
            aboutMe.append(person.getMajor());
        }

        if (person.getEmployer() != null && !person.getEmployer().trim().isEmpty()) {
            if (!aboutMe.isEmpty()) aboutMe.append(", ");
            aboutMe.append(person.getEmployer());
        }

        return aboutMe.toString();
    }
}