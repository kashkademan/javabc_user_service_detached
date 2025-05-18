package school.faang.user_service.validation.subscription;

import school.faang.user_service.exception.DataValidationException;

public class SubscriptionValidation {

    public static void validateFollowAction(long followerId, long followeeId) {
        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("Нельзя подписаться на себя");
        }
    }

    public static void validateUnfollowAction(long followerId, long followeeId) {
        if (checkFollowForSelf(followerId, followeeId)) {
            throw new DataValidationException("Нельзя отписаться от себя");
        }
    }

    public static void validateSubscribeAction(boolean existSub) {
        if (existSub) {
            throw new DataValidationException("Подписка на этого пользователя уже существует");
        }
    }

    public static void validateUnsubscribeAction(boolean existSub) {
        if (!existSub) {
            throw new DataValidationException("Нельзя отписаться от пользователя без предварительной подписки на него");
        }
    }

    private static boolean checkFollowForSelf(long followerId, long followeeId) {
        return followerId == followeeId;
    }
}
