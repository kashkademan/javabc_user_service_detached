package school.faang.user_service.entity;

public enum RequestStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED");

    private final String name;

    RequestStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}