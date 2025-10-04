package school.faang.user_service.service.career;

import school.faang.user_service.dto.career.CareerResponse;
import school.faang.user_service.dto.career.CreateCareerRequest;
import school.faang.user_service.dto.career.UpdateCareerRequest;

/**
 * Сервис для управления данными о карьере пользователей.
 * Предоставляет основные операции для работы с карьерой:
 * добавление, обновление и получение данных о карьере.
 */
public interface CareerService {

    /**
     * Добавляет новые данные о карьере для указанного пользователя.
     * Проверяет валидность данных (дата начала не в будущем,
     * дата окончания не раньше даты начала и т.д.).
     *
     * @param userId  идентификатор пользователя, для которого добавляется карьера
     * @param request DTO с данными для создания карьеры
     * @return CareerResponse с данными созданной карьеры, включая присвоенный ID
     * @throws DataValidationException если данные не прошли валидацию
     * @throws RuntimeException если пользователь с указанным ID не найден
     */
    CareerResponse addCareer(Long userId, CreateCareerRequest request);

    /**
     * Обновляет существующие данные о карьере.
     * Проверяет, что пользователь имеет право на обновление (является владельцем карьеры),
     * а также валидность обновляемых данных.
     *
     * @param userId идентификатор пользователя, выполняющего обновление
     * @param careerId идентификатор карьеры, которую необходимо обновить
     * @param request DTO с обновленными данными карьеры
     * @return CareerResponse с обновленными данными карьеры
     * @throws DataValidationException если данные не прошли валидацию
     * @throws ForbiddenException если пользователь не является владельцем карьеры
     * @throws RuntimeException если карьера с указанным ID не найдена
     */
    CareerResponse updateCareer(Long userId, long careerId, UpdateCareerRequest request);

    /**
     * Получает данные о карьере по её идентификатору.
     *
     * @param careerId идентификатор карьеры
     * @return CareerResponse с данными запрошенной карьеры
     * @throws RuntimeException если карьера с указанным ID не найдена
     */
    CareerResponse getById(long careerId);
}
