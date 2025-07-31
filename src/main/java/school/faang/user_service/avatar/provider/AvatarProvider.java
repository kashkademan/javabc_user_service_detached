package school.faang.user_service.avatar.provider;

/**
 * Интерфейс для генерации аватаров.
 * <p>
 * Реализации этого интерфейса должны предоставлять методы для генерации аватаров на основе уникального ключа.
 * </p>
 *
 * @author agent
 * @since 26.07.2025
 */
public interface AvatarProvider {
    /**
     * Генерирует InputStream случайного аватара по ключу.
     *
     * @param key уникальный ключ (например, username или UUID)
     * @return объект {@link AvatarFile}, содержащий поток SVG или PNG
     */
    AvatarFile generate(String key);
}