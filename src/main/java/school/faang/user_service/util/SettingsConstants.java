package school.faang.user_service.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SettingsConstants {
    public static final int MIN_SKILL_OFFERS = 3;
    public static final long MAX_FILE_SIZE_MB = 5;
    public static final long MAX_FILE_SIZE = MAX_FILE_SIZE_MB * 1024 * 1024;

    public static final String AVATAR_FOLDER = "avatar";
    public static final String AVATAR_MINI_FOLDER = "avatar_mini";

    public static final int MAX_SIDE_SIZE = 1080;
    public static final int MAX_SIDE_SIZE_MINI = 170;
}
