package school.faang.user_service.entity;

import lombok.Getter;

@Getter
public enum RequestStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private final String name;

    RequestStatus(String name) {
        this.name = name;
    }
}