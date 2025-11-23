package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.GetUsersDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.user.UserExperienceFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNamePatternFilter;
import school.faang.user_service.filter.user.UserPhonePatternFilter;
import school.faang.user_service.mapper.ResourceMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.ResourceRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.s3.S3ServiceImpl;
import school.faang.user_service.validation.resource.ResourceValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private final int minPasswordLength = 6;
    private final int maxAvatarFileSize = 5;
    private final int maxBigAvatarFileSideLength = 1080;
    private final int maxSmallAvatarFileSideLength = 170;
    private final UserFiltersDto userFiltersDto
            = new UserFiltersDto("Anton", "89991231213", 3, 7);
    private final UserFilter userExperienceFilter = new UserExperienceFilter();
    private final UserFilter userNamePatternFilter = new UserNamePatternFilter();
    private final UserFilter userPhonePatternFilter = new UserPhonePatternFilter();

    private final User firstUser = User.builder()
            .id(22L)
            .username("antony")
            .build();
    private final User secondUser = User.builder()
            .id(23L)
            .username("bobik")
            .build();

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final Event plannedEvent = Event.builder().status(EventStatus.PLANNED).build();
    private final Event inProgressEvent = Event.builder().status(EventStatus.IN_PROGRESS).build();
    private final Event completedEvent = Event.builder().status(EventStatus.COMPLETED).build();
    private final Event participatedEvent = Event.builder().attendees(new ArrayList<>(List.of(firstUser))).build();

    private final Goal goal = Goal.builder()
            .id(4345L)
            .users(new ArrayList<>(List.of(firstUser, secondUser)))
            .build();
    private final Goal setGoal = Goal.builder()
            .id(6632L)
            .users(new ArrayList<>(List.of(firstUser, secondUser)))
            .build();
    private final Goal menteegoal = Goal.builder().mentor(firstUser).build();
    private final Goal menteeSetGoal = Goal.builder().mentor(firstUser).build();

    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    private ArgumentCaptor<List<Goal>> saveGoalCaptor;
    @Captor
    private ArgumentCaptor<List<Goal>> deleteGoalCaptor;
    @Captor
    private ArgumentCaptor<List<Event>> saveEventCaptor;
    @Captor
    private ArgumentCaptor<List<Event>> deleteEventCaptor;
    @Captor
    private ArgumentCaptor<MultipartFile> multipartFileArgumentCaptor;
    @Captor
    private ArgumentCaptor<Resource> resourceArgumentCaptor;

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private List<UserFilter> userFilters;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private S3ServiceImpl s3Service;
    @Mock
    private DiceBearClient diceBearClient;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceValidator resourceValidator;
    @Mock
    private ResourceMapper resourceMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, countryRepository, userMapper, userContext,
                List.of(userExperienceFilter, userNamePatternFilter, userPhonePatternFilter), goalRepository,
                eventRepository, mentorshipService, s3Service, diceBearClient, resourceRepository, resourceValidator,
                resourceMapper);

        ReflectionTestUtils.setField(userService, "maxAvatarFileSize", maxAvatarFileSize);
        ReflectionTestUtils.setField(userService, "maxBigAvatarFileSideLength", maxBigAvatarFileSideLength);
        ReflectionTestUtils.setField(userService, "minPasswordLength", minPasswordLength);
        ReflectionTestUtils.setField(userService, "maxSmallAvatarFileSideLength", maxSmallAvatarFileSideLength);
    }

    @Test
    void createUserThrowsExceptionIfPasswordNotValid() {
        CreateUserDto userDto = CreateUserDto.builder()
                .password("a".repeat(minPasswordLength - 1))
                .build();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> userService.create(userDto));
        assertEquals("Password should be more than " + minPasswordLength + " symbols!",
                dataValidationException.getMessage());
    }

    @Test
    void createUserPositive() {
        Country country = Country.builder().id(123L).build();

        CreateUserDto createUserDto = CreateUserDto.builder()
                .password("a".repeat(minPasswordLength))
                .countryId(country.getId())
                .build();

        User user = User.builder()
                .id(1L)
                .username("test")
                .build();

        Resource resource = Resource.builder().id(234532L).build();

        when(diceBearClient.downloadAvatar()).thenReturn(new byte[]{});
        when(s3Service.uploadFile(Mockito.any(byte[].class), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString())).thenReturn(resource);
        when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(countryRepository.getByIdOrThrow(country.getId())).thenReturn(country);

        UserDto userDto = userService.create(createUserDto);

        assertEquals(user.getId(), userDto.id());
    }

    @Test
    void testGetUserThrowsEntityNotFound() {
        when(userRepository.getByIdOrThrow(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void testGetUser() {
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        UserDto actualUser = userService.getById(firstUser.getId());

        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(firstUser.getId(), actualUser.id());
        Assertions.assertEquals(firstUser.getUsername(), actualUser.username());
    }

    @Test
    void testGetUsersByIdsReturnEmptyListIfEmptyArgument() {
        Assertions.assertTrue(userService.getUsersByIds(null).isEmpty());
    }

    @Test
    void testGetUsersByIdsReturnEmptyListIfUsersNotFound() {
        Assertions.assertTrue(userService.getUsersByIds(GetUsersDto.builder()
                        .ids(new ArrayList<>(List.of(1L, 2L)))
                        .build())
                .isEmpty());
    }

    @Test
    void testGetUsersByIds() {
        final GetUsersDto getUsersDto = GetUsersDto.builder()
                .ids(new ArrayList<>(List.of(firstUser.getId(), secondUser.getId())))
                .build();

        when(userRepository.findAllById(getUsersDto.ids())).thenReturn(List.of(firstUser, secondUser));

        List<UserDto> actualUsers = userService.getUsersByIds(getUsersDto);
        List<UserDto> expectedUsers = new ArrayList<>(List.of(firstUser, secondUser)).stream()
                .map(userMapper::toUserDto)
                .toList();

        Assertions.assertNotNull(actualUsers);
        Assertions.assertFalse(actualUsers.isEmpty());
        Assertions.assertTrue(actualUsers.containsAll(expectedUsers));
    }


    @Test
    void testGetPremiumUsersPositive() {
        User correctUserWithMinExp = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMin())
                .build();

        User correctUserWithMaxExp = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongNameUser = User.builder()
                .username("Nikolay")
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongPhoneUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone("111111111")
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongExpUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(8)
                .build();

        Mockito.when(userRepository.findPremiumUsers()).thenReturn(Stream.of(correctUserWithMinExp,
                correctUserWithMaxExp, wrongNameUser, wrongPhoneUser, wrongExpUser));

        List<UserDto> expectedPremiumUsers = Arrays.asList(correctUserWithMinExp, correctUserWithMaxExp)
                .stream()
                .map(userMapper::toUserDto)
                .toList();
        List<UserDto> actualPremiumUsers = userService.getPremiumUsers(userFiltersDto);

        Assertions.assertEquals(2, actualPremiumUsers.size());
        Assertions.assertTrue(actualPremiumUsers.containsAll(expectedPremiumUsers));
    }

    @Test
    void testGetPremiumUsersContainsCorrectWords() {
        User wrongNameUser = User.builder()
                .username(userFiltersDto.namePattern() + "k")
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongPhoneUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern() + "2")
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongExpUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax() + 1)
                .build();

        Mockito.when(userRepository.findPremiumUsers())
                .thenReturn(Stream.of(wrongNameUser, wrongPhoneUser, wrongExpUser));

        List<UserDto> actualPremiumUsers = userService.getPremiumUsers(userFiltersDto);

        Assertions.assertEquals(0, actualPremiumUsers.size());
    }

    @Test
    void testDeactivateUserThrowsExceptionIfUserNotFound() {
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> userService.deactivateUser(firstUser.getId()));
    }

    @Test
    void testDeactivateUserThrowsExceptionIfUserAlreadyDeactivated() {
        firstUser.setActive(false);

        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> userService.deactivateUser(firstUser.getId()));
        Assertions.assertEquals("User %d already deactivated".formatted(firstUser.getId()),
                forbiddenException.getMessage());
    }

    @Test
    void testDeactivateUserPositiveWithDeleteGoals() {
        goal.setUsers(new ArrayList<>(List.of(firstUser)));
        setGoal.setUsers(new ArrayList<>(List.of(firstUser)));

        deactivateTestSteps(2);
    }

    @Test
    void testDeactivateUserPositiveWithDeleteUserFromGoal() {
        deactivateTestSteps(0);

        verify(goalRepository).deleteUserFromGoal(firstUser.getId(), goal.getId());
        verify(goalRepository).deleteUserFromGoal(firstUser.getId(), setGoal.getId());

    }

    @Test
    void testActivateUserThrowsExceptionIfUserNotFound() {
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> userService.activateUser(firstUser.getId()));
    }

    @Test
    void testActivateUserThrowsExceptionIfUserAlreadyDeactivated() {
        firstUser.setActive(true);

        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> userService.activateUser(firstUser.getId()));
        Assertions.assertEquals("User %d already activated".formatted(firstUser.getId()),
                forbiddenException.getMessage());
    }

    @Test
    void testActivateUserPositive() {
        firstUser.setActive(false);

        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        userService.activateUser(firstUser.getId());

        verify(userRepository).save(userCaptor.capture());

        Assertions.assertTrue(userCaptor.getValue().isActive());
    }

    private void deactivateTestSteps(int deleteGoalsSize) {
        firstUser.setActive(true);
        firstUser.setGoals(new ArrayList<>(List.of(goal)));
        firstUser.setSetGoals(new ArrayList<>(List.of(setGoal)));
        firstUser.setOwnedEvents(new ArrayList<>(List.of(plannedEvent, inProgressEvent, completedEvent)));
        firstUser.setParticipatedEvents(new ArrayList<>(List.of(participatedEvent)));
        firstUser.setMentees(new ArrayList<>(List.of(secondUser)));

        secondUser.setGoals(new ArrayList<>(List.of(menteegoal)));
        secondUser.setSetGoals(new ArrayList<>(List.of(menteeSetGoal)));

        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        userService.deactivateUser(firstUser.getId());

        verify(userRepository).save(userCaptor.capture());
        verify(goalRepository).saveAll(saveGoalCaptor.capture());
        verify(goalRepository).deleteAll(deleteGoalCaptor.capture());
        verify(eventRepository).saveAll(saveEventCaptor.capture());
        verify(eventRepository).deleteAll(deleteEventCaptor.capture());
        verify(mentorshipService).deleteMentorship(secondUser.getId(), firstUser.getId());

        User deactivatedUser = userCaptor.getValue();
        int canceledEventsSize = deactivatedUser.getOwnedEvents().stream().filter(event ->
                event.getStatus().equals(EventStatus.CANCELED)).toList().size();

        Assertions.assertFalse(deactivatedUser.isActive());
        Assertions.assertEquals(1, deactivatedUser.getParticipatedEvents().size());
        Assertions.assertEquals(firstUser.getId(), deactivatedUser.getId());
        Assertions.assertEquals(2, canceledEventsSize);
        Assertions.assertEquals(deleteGoalsSize, deleteGoalCaptor.getValue().size());
        Assertions.assertEquals(2, saveGoalCaptor.getValue().size());
        Assertions.assertEquals(3, saveEventCaptor.getValue().size());
        Assertions.assertEquals(2, deleteEventCaptor.getValue().size());

        List<Long> participatedEventAttendees = deactivatedUser.getParticipatedEvents().get(0).getAttendees().stream()
                .map(User::getId).toList();
        if (!participatedEventAttendees.isEmpty()) {
            Assertions.assertFalse(participatedEventAttendees.contains(firstUser.getId()));
        }
    }

    @Test
    void testBanUserPositive() {
        List<Long> usersIds = List.of(1L, 2L);

        userService.banUsers(usersIds);

        verify(userRepository).banUsers(eq(usersIds));
    }

    @Test
    void testGetNotBannedUsersIdsReturnEmptyList() {
        User user = User.builder()
                .id(1L)
                .banned(true)
                .build();

        when(userRepository.findAllById(List.of(user.getId()))).thenReturn(List.of(user));

        List<Long> notBannedUsersIds = userService.getNotBannedUsersIds(List.of(user.getId()));

        Assertions.assertTrue(notBannedUsersIds.isEmpty());
    }

    @Test
    void testGetNotBannedUsersIdsPositive() {
        User user = User.builder()
                .id(1L)
                .banned(false)
                .build();

        when(userRepository.findAllById(List.of(user.getId()))).thenReturn(List.of(user));

        List<Long> notBannedUsersIds = userService.getNotBannedUsersIds(List.of(user.getId()));

        Assertions.assertEquals(1, notBannedUsersIds.size());
        Assertions.assertEquals(user.getId(), notBannedUsersIds.get(0));
    }

    @Test
    void testAddAvatar() {
        Resource bigAvatarResource = new Resource();
        Resource smallAvatarResource = new Resource();
        bigAvatarResource.setId(1L);
        bigAvatarResource.setKey("bigAvatarResource");
        smallAvatarResource.setId(2L);
        smallAvatarResource.setKey("smallAvatarResource");

        MultipartFile bigAvatarFile = new MockMultipartFile("bigAvatarFile", new byte[]{});
        MultipartFile smallAvatarFile = new MockMultipartFile("smallAvatarFile", new byte[]{});

        when(userContext.getUserId()).thenReturn(firstUser.getId());
        when(resourceValidator.validateImageDimensions(Mockito.any(MultipartFile.class),
                eq(maxBigAvatarFileSideLength), eq(maxBigAvatarFileSideLength))).thenReturn(bigAvatarFile);
        when(resourceValidator.validateImageDimensions(Mockito.any(MultipartFile.class),
                eq(maxSmallAvatarFileSideLength), eq(maxSmallAvatarFileSideLength))).thenReturn(smallAvatarFile);
        when(userRepository.getByIdOrThrow(anyLong())).thenReturn(firstUser);
        when(s3Service.uploadFile(eq(bigAvatarFile), anyString())).thenReturn(bigAvatarResource);
        when(s3Service.uploadFile(eq(smallAvatarFile), anyString())).thenReturn(smallAvatarResource);
        when(resourceRepository.save(eq(bigAvatarResource))).thenReturn(bigAvatarResource);
        when(resourceRepository.save(eq(smallAvatarResource))).thenReturn(smallAvatarResource);

        MultipartFile fileToAdd = new MockMultipartFile("fileToAdd", new byte[]{});
        userService.addAvatar(fileToAdd);

        verify(resourceValidator, times(2))
                .validateImageDimensions(eq(fileToAdd), anyInt(), anyInt());
        verify(s3Service, times(2)).uploadFile(multipartFileArgumentCaptor.capture(),
                eq(firstUser.getId() + firstUser.getUsername()));
        verify(resourceRepository, times(2)).save(resourceArgumentCaptor.capture());
        verify(userRepository).save(userCaptor.capture());

        List<MultipartFile> allMultipartFileCaptures = multipartFileArgumentCaptor.getAllValues();
        List<Resource> allResourceCaptures = resourceArgumentCaptor.getAllValues();
        User capturedUser = userCaptor.getValue();

        assertTrue(allMultipartFileCaptures.containsAll(List.of(bigAvatarFile, smallAvatarFile)));
        assertTrue(allResourceCaptures.containsAll(List.of(bigAvatarResource, smallAvatarResource)));
        assertEquals(firstUser.getId(), capturedUser.getId());
    }

    @Test
    void testGetAvatarWhenUserHasBigAvatarShouldReturnFile() {
        MultipartFile bigAvatarFile = new MockMultipartFile("bigAvatarFile", new byte[]{});
        UserProfilePic userProfilePic = mock(UserProfilePic.class);
        User firstUser = User.builder()
                .id(22L)
                .username("antony")
                .userProfilePic(userProfilePic)
                .build();

        when(userContext.getUserId()).thenReturn(firstUser.getId());
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);
        when(userProfilePic.getFileId()).thenReturn(bigAvatarFile.getName());
        when(s3Service.getFile(bigAvatarFile.getName())).thenReturn(bigAvatarFile);

        MultipartFile result = userService.getAvatar();

        assertEquals(result, bigAvatarFile);
        verify(s3Service).getFile(bigAvatarFile.getName());
    }

    @Test
    void testGetAvatarWhenUserHasOnlySmallAvatarShouldReturnFile() {
        MultipartFile smallAvatarFile = new MockMultipartFile("smallAvatarFile", new byte[]{});
        UserProfilePic userProfilePic = new UserProfilePic("", smallAvatarFile.getName());
        User firstUser = User.builder()
                .id(22L)
                .username("antony")
                .userProfilePic(userProfilePic)
                .build();

        when(userContext.getUserId()).thenReturn(firstUser.getId());
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);
        when(userContext.getUserId()).thenReturn(firstUser.getId());
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);
        when(s3Service.getFile(smallAvatarFile.getName())).thenReturn(smallAvatarFile);

        MultipartFile result = userService.getAvatar();

        assertEquals(result, smallAvatarFile);
        verify(s3Service).getFile(smallAvatarFile.getName());
    }

    @Test
    void testGetAvatarWhenUserHasNoAvatarShouldThrowException() {
        User firstUser = User.builder()
                .id(22L)
                .userProfilePic(new UserProfilePic())
                .build();

        when(userContext.getUserId()).thenReturn(firstUser.getId());
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getAvatar());
        assertTrue(entityNotFoundException.getMessage().contains("User %d hasn't avatar".formatted(firstUser.getId())));
    }

    @Test
    void testGetAvatarWhenUserProfilePicIsNullShouldThrowException() {
        when(userContext.getUserId()).thenReturn(firstUser.getId());
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getAvatar());
        assertTrue(entityNotFoundException.getMessage().contains("User %d hasn't avatar".formatted(firstUser.getId())));
    }

    @Test
    void testDeleteAvatarWhenUserHasOnlySmallAvatarShouldDeleteSmall() {
        User user = User.builder()
                .id(1L)
                .userProfilePic(new UserProfilePic("", "small-key"))
                .build();

        Resource smallResource = new Resource();
        smallResource.setId(2L);

        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.getByIdOrThrow(user.getId())).thenReturn(user);
        when(resourceRepository.findByKey(user.getUserProfilePic().getSmallFileId()))
                .thenReturn(Optional.of(smallResource));

        userService.deleteAvatar();

        verify(s3Service).deleteFile("small-key");
        verify(resourceRepository).deleteById(2L);
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteAvatarWhenUserHasBothAvatarsShouldDeleteBoth() {
        User user = User.builder()
                .id(1L)
                .userProfilePic(new UserProfilePic("big-key", "small-key"))
                .build();

        Resource bigResource = new Resource();
        bigResource.setId(1L);
        Resource smallResource = new Resource();
        smallResource.setId(2L);

        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.getByIdOrThrow(user.getId())).thenReturn(user);
        when(resourceRepository.findByKey(user.getUserProfilePic().getFileId())).thenReturn(Optional.of(bigResource));
        when(resourceRepository.findByKey(user.getUserProfilePic().getSmallFileId()))
                .thenReturn(Optional.of(smallResource));

        userService.deleteAvatar();

        verify(s3Service).deleteFile("big-key");
        verify(s3Service).deleteFile("small-key");
        verify(resourceRepository).deleteById(bigResource.getId());
        verify(resourceRepository).deleteById(smallResource.getId());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteAvatarWhenUserHasOnlyBigAvatarShouldDeleteBig() {
        User user = User.builder()
                .id(1L)
                .userProfilePic(new UserProfilePic("big-key", ""))
                .build();

        Resource bigResource = new Resource();
        bigResource.setId(1L);

        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.getByIdOrThrow(user.getId())).thenReturn(user);
        when(resourceRepository.findByKey(user.getUserProfilePic().getFileId())).thenReturn(Optional.of(bigResource));

        userService.deleteAvatar();

        verify(s3Service).deleteFile(eq("big-key"));
        verify(resourceRepository).deleteById(bigResource.getId());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteAvatarWhenUserHasNoAvatarsShouldThrowException() {
        User user = User.builder()
                .id(1L)
                .userProfilePic(new UserProfilePic("", ""))
                .build();

        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.getByIdOrThrow(user.getId())).thenReturn(user);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getAvatar());
        assertTrue(entityNotFoundException.getMessage().contains("User %d hasn't avatar".formatted(user.getId())));
    }

    @Test
    void testDeleteAvatarWhenUserProfilePicIsNullShouldThrowException() {
        User user = User.builder()
                .id(1L)
                .userProfilePic(null)
                .build();

        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.getByIdOrThrow(user.getId())).thenReturn(user);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getAvatar());
        assertTrue(entityNotFoundException.getMessage().contains("User %d hasn't avatar".formatted(user.getId())));
    }
}