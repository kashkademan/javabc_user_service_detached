package school.faang.user_service.annotation;

import school.faang.user_service.config.kafka.enums.SubscriptionEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishSubscriptionEventKafka {
    SubscriptionEventType type();
}