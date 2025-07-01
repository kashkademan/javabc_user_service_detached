package school.faang.user_service.service.career;

import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

public interface CareerService {

    /**
     * Создает новую карьерную запись для указанного сотрудника.
     *
     * @param careerDto DTO-объект с данными для создания карьерной записи
     * @param userId пользователь который хочет добавить карьеру
     * @return созданная карьерная запись
     * @throws DataValidationException если дата больше чем настоящее
     */
    CareerDto addCareer(long userId, CareerDto careerDto);

    /**
     * Обновляет существующую карьерную запись.
     *
     * @param userId идентификатор карьерной записи для обновления
     * @param careerDto DTO-объект с обновленными данными
     * @return обновленная карьерная запись
     * @throws DataValidationException если дата больше чем настоящее
     * @throws ForbiddenException если пользователь пытается обновить не свои данные
     */
    CareerDto updateCareer(long userId, long careerId, CareerDto careerDto);

    /**
     * Возвращает карьерную запись по указанному идентификатору.
     *
     * @param careerId  идентификатор карьерной записи
     * @return карьерная запись
     */
    CareerDto getById(long careerId);
}
