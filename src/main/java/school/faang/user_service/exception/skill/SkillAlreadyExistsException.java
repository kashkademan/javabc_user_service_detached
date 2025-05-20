package school.faang.user_service.exception.skill;

public class SkillAlreadyExistsException extends RuntimeException {

    public SkillAlreadyExistsException(String message) {
        super(message);
    }
}
