package school.faang.user_service.exception.skill;

public class SkillNotExistException extends RuntimeException {

    public SkillNotExistException(String skillsId) {
        super("Contains skills [%s] that not exist!!".formatted(skillsId));
    }

    public SkillNotExistException(Long skillId) {
        super("Skill with id - '%d' not exist!!".formatted(skillId));
    }
}