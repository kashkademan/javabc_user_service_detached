package school.faang.user_service.exception.skill;

import java.util.NoSuchElementException;

public class SkillNotFoundException extends NoSuchElementException {
    public SkillNotFoundException(long skillId) {
        super(String.format("Skill with id %d not found", skillId));
    }
}
