package school.faang.user_service.exception;

public enum MessageError {
    RECOMMEND_REQUEST_NOT_FOUND_EXCEPTION("The recommendation request by ID was not found."),
    USER_NOT_FOUND_EXCEPTION("User by ID is not found");

    private final String message;

    MessageError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
