package school.faang.user_service.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DiceBearStyles {
    ADVENTURER("adventurer"),
    ADVENTURER_NEUTRAL("adventurer-neutral"),
    AVATAAARS("avataaars"),
    BIG_EARS("big-ears"),
    BOTTTS("bottts"),
    CROODLES("croodles"),
    FUN_EMOJI("fun-emoji"),
    ICONS("icons"),
    IDENTICON("identicon"),
    LORELEI("lorelei"),
    MICAH("micah"),
    MINIAVS("miniavs"),
    OPEN_PEEPS("open-peeps"),
    PERSONAS("personas"),
    PIXEL_ART("pixel-art"),
    SHAPES("shapes"),
    THUMBS("thumbs");

    private final String styleName;
}
