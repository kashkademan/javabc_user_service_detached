package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.GetUsersDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final List<UserFilter> userFilters;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;

    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, UpdateUserDto userDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(GetUsersDto getUsersDto) {
        if (getUsersDto == null) {
            return new ArrayList<>();
        }

        return userRepository.findAllById(getUsersDto.ids()).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getPremiumUsers(UserFiltersDto userFiltersDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();

        if (userFiltersDto == null) {
            return premiumUsers.map(userMapper::toUserDto).toList();
        }

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(userFiltersDto)) {
                premiumUsers = userFilter.apply(premiumUsers, userFiltersDto);
            }
        }
        return premiumUsers.map(userMapper::toUserDto).toList();
    }

    @Override
    public void deactivateUser(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        List<Goal> goalsToDelete = new ArrayList<>();

        if (!user.isActive()) {
            throw new ForbiddenException("User %d already deactivated".formatted(userId));
        }

        for (Goal goal : user.getGoals()) {
            if (goal.getUsers().size() > 1) {
                goalRepository.deleteUserFromGoal(userId, goal.getId());
            } else {
                goalsToDelete.add(goal);
            }
        }

        for (Goal setGoal : user.getSetGoals()) {
            if (setGoal.getUsers().size() > 1) {
                goalRepository.deleteUserFromGoal(userId, setGoal.getId());
            } else {
                goalsToDelete.add(setGoal);
            }
        }

        List<Event> eventsToSave = new ArrayList<>();
        List<Event> eventsToDelete = new ArrayList<>();
        for (Event ownedEvent : user.getOwnedEvents()) {
            if (ownedEvent.getStatus().equals(EventStatus.PLANNED)
                    || ownedEvent.getStatus().equals(EventStatus.IN_PROGRESS)) {
                ownedEvent.setStatus(EventStatus.CANCELED);
                ownedEvent.setUpdatedAt(LocalDateTime.now());
                eventsToDelete.add(ownedEvent);
                eventsToSave.add(ownedEvent);
            }
        }

        for (Event participatedEvent : user.getParticipatedEvents()) {
            List<User> attendees = participatedEvent.getAttendees();
            attendees.remove(user);
            participatedEvent.setAttendees(attendees);
            eventsToSave.add(participatedEvent);
        }

        List<Goal> goalsToSave = new ArrayList<>();
        for (User mentee : user.getMentees()) {
            mentorshipService.deleteMentorship(mentee.getId(), user.getId());

            for (Goal goal : mentee.getGoals()) {
                if (goal.getMentor().getId().equals(user.getId())) {
                    goal.setMentor(null);
                    goalsToSave.add(goal);
                }
            }

            for (Goal setGoal : mentee.getSetGoals()) {
                if (setGoal.getMentor().getId().equals(user.getId())) {
                    setGoal.setMentor(null);
                    goalsToSave.add(setGoal);
                }
            }
        }

        user.setActive(false);

        userRepository.save(user);
        log.info("User {} has been deactivated", user.getId());

        List<Goal> updatedGoals = goalRepository.saveAll(goalsToSave);
        if (!updatedGoals.isEmpty()) {
            log.info("Removed mentors from goals {}", updatedGoals.stream().map(Goal::getId).toList());
        }

        goalRepository.deleteAll(goalsToDelete);
        if (!goalsToDelete.isEmpty()) {
            log.info("Removed goals {}", goalsToDelete.stream().map(Goal::getId).toList());
        }

        List<Event> canceledEvents = eventRepository.saveAll(eventsToSave);
        if (!canceledEvents.isEmpty()) {
            log.info("Canceled events {}", canceledEvents.stream().map(Event::getId).toList());
        }

        eventRepository.deleteAll(eventsToDelete);
        if (!eventsToDelete.isEmpty()) {
            log.info("Removed events {}", eventsToDelete.stream().map(Event::getId).toList());
        }
    }

    @Override
    public void activateUser(long userId) {
        User user = userRepository.getByIdOrThrow(userId);

        if (user.isActive()) {
            throw new ForbiddenException("User %d already activated".formatted(userId));
        }

        user.setActive(true);
        userRepository.save(user);
        log.info("User {} has been activated", user.getId());
    }
}