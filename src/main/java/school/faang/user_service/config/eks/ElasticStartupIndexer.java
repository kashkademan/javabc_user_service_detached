package school.faang.user_service.config.eks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.elastic.UserElasticService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticStartupIndexer {

    private final UserElasticService userElasticService;
    private final UserService userService;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        log.info("Started startup bulking Elastic Search with existing in database users");
        List<User> users = userService.getAllUsers();
        userElasticService.bulkUsers(users);
    }
}
