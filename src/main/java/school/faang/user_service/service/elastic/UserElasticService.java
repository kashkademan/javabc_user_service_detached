package school.faang.user_service.service.elastic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.elastic_search.UserDocument;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.elastic_search.UserDocumentRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserElasticService {

    private final UserDocumentRepository userDocumentRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public List<User> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDocument> documents = userDocumentRepository.searchByQuery(query, pageable);
        documents.forEach(System.out::println);
        List<Long> userIds = documents.stream()
                .map(UserDocument::getId)
                .toList();
        return userService.collectUsersByIds(userIds);
    }


    public void bulkUsers(List<User> users) {
        List<UserDocument> documents = userMapper.toDocumentList(users);
        userDocumentRepository.saveAll(documents);
        log.info("Fetched all users from database and bulked them into Elastic Search");
    }

}
