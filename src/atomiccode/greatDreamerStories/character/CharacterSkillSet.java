package atomiccode.greatDreamerStories.character;

import java.util.Collections;
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.List;
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
        return formatSkills(0, skills);
    }

    public String formatSkills(int skillsPerLine, CharacterSkill... skills) {
        return String.join("\n", formatSkillLines(skillsPerLine, skills));
    }

    public List<String> formatSkillLines(int skillsPerLine, CharacterSkill... skills) {
        List<String> lines = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < skills.length; i++) {
            if (skillsPerLine > 0 && i > 0 && i % skillsPerLine == 0) {
                lines.add(builder.toString());
                builder = new StringBuilder();
            } else if (builder.length() > 0) {
                builder.append("  ");
            }

            CharacterSkill skill = skills[i];
            builder.append(skill.getCode()).append(":").append(getValue(skill));
        }
        if (builder.length() > 0) {
            lines.add(builder.toString());
        }
        return lines;
    }

    private int clamp(int value, CharacterSkill skill) {
        int minimum = skill == CharacterSkill.CTHULHU_MYTHOS ? 0 : 1;
        return Math.max(minimum, Math.min(99, value));
    }
}
