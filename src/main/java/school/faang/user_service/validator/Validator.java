package school.faang.user_service.validator;

public interface Validator<T> {
    void validate(T t);
}
