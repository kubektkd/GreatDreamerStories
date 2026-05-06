package atomiccode.greatDreamerStories.character;

import atomiccode.greatDreamerStories.i18n.GameTexts;

import java.util.Locale;

public enum CharacterSkill {
    ACCOUNTING(5),
    ANTHROPOLOGY(1),
    APPRAISE(5),
    ARCHAEOLOGY(1),
    ART_CRAFT(5),
    CHARM(15),
    CLIMB(20),
    COMPUTER_USE(5),
    CREDIT_RATING(0),
    CTHULHU_MYTHOS(0),
    DISGUISE(5),
    DODGE(25),
    DRIVE_AUTO(20),
    ELECTRICAL_REPAIR(10),
    FAST_TALK(5),
    FIGHTING_BRAWL(25),
    FIREARMS_HANDGUN(20),
    FIREARMS_RIFLE_SHOTGUN(25),
    FIRST_AID(30),
    HISTORY(5),
    INTIMIDATE(15),
    JUMP(20),
    LANGUAGE_OWN(25),
    LANGUAGE_OTHER(1),
    LAW(5),
    LIBRARY_USE(20),
    LISTEN(20),
    LOCKSMITH(1),
    MECHANICAL_REPAIR(10),
    MEDICINE(1),
    NATURAL_WORLD(10),
    NAVIGATE(10),
    OCCULT(5),
    PERSUADE(10),
    PSYCHOANALYSIS(1),
    PSYCHOLOGY(10),
    RIDE(5),
    SCIENCE(1),
    SLEIGHT_OF_HAND(10),
    SPOT_HIDDEN(25),
    STEALTH(20),
    SURVIVAL(10),
    SWIM(20),
    THROW(20),
    TRACK(10);

    private final int baseValue;

    CharacterSkill(int baseValue) {
        this.baseValue = baseValue;
    }

    public String getDisplayName() {
        return GameTexts.tr("skill." + name().toLowerCase(Locale.ROOT));
    }

    /** Short table / sheet label; from {@code skill.<name>.code} in locale files. */
    public String getCode() {
        return GameTexts.tr("skill." + name().toLowerCase(Locale.ROOT) + ".code");
    }

    public int getBaseValue() {
        return baseValue;
    }
}
