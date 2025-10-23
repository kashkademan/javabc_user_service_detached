package school.faang.user_service.validation;

public class ValidationConstants {
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int DESCRIPTION_MAX_LENGTH = 4096;

    public static final String TITLE_SIZE_MESSAGE = "Title must be less than " + TITLE_MAX_LENGTH + " characters";
    public static final String DESCRIPTION_SIZE_MESSAGE = "Description must be less than "
            + DESCRIPTION_MAX_LENGTH + " characters";
}
