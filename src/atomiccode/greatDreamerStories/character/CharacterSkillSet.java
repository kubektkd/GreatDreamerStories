package atomiccode.greatDreamerStories.character;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class CharacterSkillSet {
    private final EnumMap<CharacterSkill, Integer> values;

    public CharacterSkillSet(Map<CharacterSkill, Integer> values) {
        this.values = new EnumMap<>(CharacterSkill.class);
        for (CharacterSkill skill : CharacterSkill.values()) {
            this.values.put(skill, clamp(values.getOrDefault(skill, skill.getBaseValue()), skill));
        }
    }

    public int getValue(CharacterSkill skill) {
        return values.getOrDefault(skill, skill.getBaseValue());
    }

    public Map<CharacterSkill, Integer> asMap() {
        return Collections.unmodifiableMap(values);
    }

    public String formatSkills(CharacterSkill... skills) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < skills.length; i++) {
            if (i > 0) {
                builder.append("  ");
            }
            CharacterSkill skill = skills[i];
            builder.append(skill.getCode()).append(":").append(getValue(skill));
        }
        return builder.toString();
    }

    private int clamp(int value, CharacterSkill skill) {
        int minimum = skill == CharacterSkill.CTHULHU_MYTHOS ? 0 : 1;
        return Math.max(minimum, Math.min(99, value));
    }
}
