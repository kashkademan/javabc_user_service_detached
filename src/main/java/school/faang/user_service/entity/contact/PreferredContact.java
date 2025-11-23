package school.faang.user_service.entity.contact;

import lombok.Getter;

import java.util.Map;

@Getter
public enum PreferredContact {
    EMAIL(0),
    PHONE(1),
    TELEGRAM(2);

    private final int code;

    private static final Map<Integer, PreferredContact> BY_CODE =
            Map.of(
                    0, EMAIL,
                    1, PHONE,
                    2, TELEGRAM
            );

    PreferredContact(int code) {
        this.code = code;
    }

    public static PreferredContact fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        PreferredContact result = BY_CODE.get(code);
        if (result == null) {
            throw new IllegalArgumentException("Unknown PreferredContact code: " + code);
        }
        return result;
    }

    public static PreferredContact fromString(String preference) {
        for (PreferredContact contact : PreferredContact.values()) {
            if (contact.name().equalsIgnoreCase(preference)) {
                return contact;
            }
        }
        throw new IllegalArgumentException("No contact preference with name " + preference + " found");
    }
}