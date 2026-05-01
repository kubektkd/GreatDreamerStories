package atomiccode.greatDreamerStories.character;

import java.util.EnumMap;

public class SkillCalculator {
    private static final int MIN_SKILL_VALUE = 1;
    private static final int MAX_SKILL_VALUE = 99;

    private SkillCalculator() {
    }

    public static CharacterSkillSet calculateForPoliceOfficer(Character character) {
        return calculate(character, OccupationProfile.modernScandinavianPoliceOfficer());
    }

    public static CharacterSkillSet calculate(Character character, OccupationProfile occupation) {
        EnumMap<CharacterSkill, Integer> values = new EnumMap<>(CharacterSkill.class);

        for (CharacterSkill skill : CharacterSkill.values()) {
            int value = getBaseValue(skill, character);
            value += getAttributeBonus(skill, character);
            value += occupation.getBonus(skill);
            values.put(skill, clamp(value, skill));
        }

        return new CharacterSkillSet(values);
    }

    private static int getBaseValue(CharacterSkill skill, Character character) {
        switch (skill) {
            case DODGE:
                return character.getDexterity() / 2;
            case LANGUAGE_OWN:
                return character.getEducation();
            default:
                return skill.getBaseValue();
        }
    }

    private static int getAttributeBonus(CharacterSkill skill, Character character) {
        switch (skill) {
            case ACCOUNTING:
            case COMPUTER_USE:
            case LAW:
            case LIBRARY_USE:
            case MEDICINE:
            case SCIENCE:
                return mentalBonus(character);
            case ANTHROPOLOGY:
            case APPRAISE:
            case ARCHAEOLOGY:
            case HISTORY:
            case LANGUAGE_OWN:
            case NATURAL_WORLD:
            case OCCULT:
                return knowledgeBonus(character);
            case ART_CRAFT:
            case CHARM:
            case CREDIT_RATING:
            case FAST_TALK:
            case PERSUADE:
                return socialBonus(character);
            case CLIMB:
            case FIGHTING_BRAWL:
            case INTIMIDATE:
            case JUMP:
            case SWIM:
            case THROW:
                return physicalBonus(character);
            case DISGUISE:
            case DODGE:
            case DRIVE_AUTO:
            case FIREARMS_HANDGUN:
            case FIREARMS_RIFLE_SHOTGUN:
            case LOCKSMITH:
            case RIDE:
            case SLEIGHT_OF_HAND:
            case STEALTH:
                return dexterityBonus(character);
            case FIRST_AID:
            case LISTEN:
            case PSYCHOANALYSIS:
            case PSYCHOLOGY:
            case SPOT_HIDDEN:
            case TRACK:
                return investigationBonus(character);
            case ELECTRICAL_REPAIR:
            case MECHANICAL_REPAIR:
            case NAVIGATE:
            case SURVIVAL:
                return practicalBonus(character);
            case CTHULHU_MYTHOS:
            default:
                return 0;
        }
    }

    private static int mentalBonus(Character character) {
        return modifier(character.getIntelligence()) + modifier(character.getEducation());
    }

    private static int knowledgeBonus(Character character) {
        return modifier(character.getEducation()) + modifier(character.getIntelligence()) / 2;
    }

    private static int socialBonus(Character character) {
        return modifier(character.getAppearance()) + modifier(character.getPower()) / 2 + modifier(character.getEducation()) / 2;
    }

    private static int physicalBonus(Character character) {
        return modifier(character.getStrength()) + modifier(character.getSize()) / 2 + modifier(character.getConstitution()) / 2;
    }

    private static int dexterityBonus(Character character) {
        return modifier(character.getDexterity()) + modifier(character.getIntelligence()) / 2;
    }

    private static int investigationBonus(Character character) {
        return modifier(character.getIntelligence()) + modifier(character.getPower()) / 2 + modifier(character.getEducation()) / 2;
    }

    private static int practicalBonus(Character character) {
        return modifier(character.getIntelligence()) + modifier(character.getDexterity()) / 2 + modifier(character.getEducation()) / 2;
    }

    private static int modifier(int attribute) {
        return (attribute - Character.INITIAL_STAT_VALUE) / 5;
    }

    private static int clamp(int value, CharacterSkill skill) {
        int minimum = skill == CharacterSkill.CTHULHU_MYTHOS ? 0 : MIN_SKILL_VALUE;
        return Math.max(minimum, Math.min(MAX_SKILL_VALUE, value));
    }
}
