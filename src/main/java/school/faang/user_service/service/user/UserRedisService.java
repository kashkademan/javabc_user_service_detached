package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.user.UserRedisMapper;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.model.redis.user.UserRedisModel;
import school.faang.user_service.repository.user.UserRedisRepository;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRedisService {
    private final UserRedisRepository userRedisRepository;
    private final UserRedisMapper userRedisMapper;

    public void saveUser(User user, long ttl) {
        UserRedisModel userRedisModel = userRedisMapper.toUserRedisModel(user);
        log.debug("Mapping User entity to UserRedisModel. Entity content: {}. RedisModel content: {}.",
                user, userRedisModel);

        userRedisModel.setTtl(ttl);

        String userKey = RedisKeyUtil.getKeyById(user.getId(), RedisHashType.USER);
        userRedisModel.setKey(userKey);

        UserRedisModel savedUser = userRedisRepository.save(userRedisModel);
        log.info("User {} has been saved in redis", savedUser);
    }

    public Optional<User> getUserFromRedisById(long userId) {
        String userKey = RedisKeyUtil.getKeyById(userId, RedisHashType.USER);

        return userRedisRepository.findById(userKey)
                .map(userRedisMapper::toUserEntity)
                .or(Optional::empty);
    }

    
    // TODO: использовать
    public void updatePromotedUser(User user) {
        UserRedisModel userRedisModel = userRedisMapper.toUserRedisModel(user);
        UserRedisModel savedUser = userRedisRepository.save(userRedisModel);
        log.info("User {} has been updated in redis", savedUser);
    }
}
