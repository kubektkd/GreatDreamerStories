package atomiccode.greatDreamerStories.character;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class OccupationProfile {
    private static final OccupationProfile MODERN_SCANDINAVIAN_POLICE_OFFICER = createPoliceOfficerProfile();

    private final String displayName;
    private final EnumMap<CharacterSkill, Integer> skillBonuses;

    private OccupationProfile(String displayName, EnumMap<CharacterSkill, Integer> skillBonuses) {
        this.displayName = displayName;
        this.skillBonuses = skillBonuses;
    }

    public static OccupationProfile modernScandinavianPoliceOfficer() {
        return MODERN_SCANDINAVIAN_POLICE_OFFICER;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBonus(CharacterSkill skill) {
        return skillBonuses.getOrDefault(skill, 0);
    }

    public Map<CharacterSkill, Integer> getSkillBonuses() {
        return Collections.unmodifiableMap(skillBonuses);
    }

    private static OccupationProfile createPoliceOfficerProfile() {
        EnumMap<CharacterSkill, Integer> bonuses = new EnumMap<>(CharacterSkill.class);
        bonuses.put(CharacterSkill.LAW, 35);
        bonuses.put(CharacterSkill.PSYCHOLOGY, 25);
        bonuses.put(CharacterSkill.SPOT_HIDDEN, 25);
        bonuses.put(CharacterSkill.LISTEN, 20);
        bonuses.put(CharacterSkill.FIRST_AID, 20);
        bonuses.put(CharacterSkill.FIREARMS_HANDGUN, 20);
        bonuses.put(CharacterSkill.FIGHTING_BRAWL, 15);
        bonuses.put(CharacterSkill.DRIVE_AUTO, 15);
        bonuses.put(CharacterSkill.PERSUADE, 15);
        bonuses.put(CharacterSkill.INTIMIDATE, 15);
        bonuses.put(CharacterSkill.COMPUTER_USE, 12);
        bonuses.put(CharacterSkill.LIBRARY_USE, 12);
        bonuses.put(CharacterSkill.TRACK, 12);
        bonuses.put(CharacterSkill.FAST_TALK, 8);
        bonuses.put(CharacterSkill.MEDICINE, 8);
        bonuses.put(CharacterSkill.NAVIGATE, 8);
        bonuses.put(CharacterSkill.CREDIT_RATING, 25);
        return new OccupationProfile("Chief Police Officer", bonuses);
    }
}
