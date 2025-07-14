package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> filters;

    @Transactional(readOnly = true)
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<User> getUsers(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<User> getPremiumUsers() {
        return getPremiumUsers(null);
    }

    @Transactional(readOnly = true)
    public List<User> getPremiumUsers(UserFilterDto presetFilterDto) {
        Stream<User> users = userRepository.findPremiumUsers();

        if (null == presetFilterDto) {
            return users.toList();
        }

        return filtersApply(users, presetFilterDto);
    }

    public Page<User> getActiveUsers(boolean active, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllByActive(active, pageable);
    }

    private List<User> filtersApply(Stream<User> users, UserFilterDto presetFilterDto) {
        for (UserFilter filter : filters) {
            if (filter.isApplicable(presetFilterDto)) {
                users = filter.apply(users, presetFilterDto);
            }
        }
        return users.toList();
    }
}