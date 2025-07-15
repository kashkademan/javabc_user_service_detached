package school.faang.user_service.dto.premium;

/**
 * PaymentStatus — перечисление, представляющее возможные статусы платежа.
 * <p>
 * Используется для обозначения результата попытки проведения платежа.
 * На данный момент содержит только одно значение:
 * <ul>
 *   <li>{@link #SUCCESS} — платеж успешно выполнен.</li>
 * </ul>
 * В дальнейшем можно расширить другими статусами, например, FAILURE, PENDING и т.д.
 * </p>
 *
 * @author agent
 * @since 10.07.2025
 */
public enum PaymentStatus {
    SUCCESS
}