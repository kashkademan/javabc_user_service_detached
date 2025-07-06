package school.faang.user_service.entity.contact;

public enum ContactType {
    GITHUB("gitHub"),
    TELEGRAM("Telegram"),
    VK("VK"),
    FACEBOOK("Facebook"),
    INSTAGRAM("Instagram"),
    WHATSAPP("WhatsApp"),
    CUSTOM("Custom");

    private final String name;

    ContactType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}