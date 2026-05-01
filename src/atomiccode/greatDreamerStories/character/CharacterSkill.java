package atomiccode.greatDreamerStories.character;

public enum CharacterSkill {
    ACCOUNTING("Accounting", "ACC", 5),
    ANTHROPOLOGY("Anthropology", "ANT", 1),
    APPRAISE("Appraise", "APR", 5),
    ARCHAEOLOGY("Archaeology", "ARC", 1),
    ART_CRAFT("Art/Craft", "ART", 5),
    CHARM("Charm", "CHM", 15),
    CLIMB("Climb", "CLB", 20),
    COMPUTER_USE("Computer Use", "CMP", 5),
    CREDIT_RATING("Credit Rating", "CR", 0),
    CTHULHU_MYTHOS("Cthulhu Mythos", "MYT", 0),
    DISGUISE("Disguise", "DSG", 5),
    DODGE("Dodge", "DOD", 0),
    DRIVE_AUTO("Drive Auto", "DRV", 20),
    ELECTRICAL_REPAIR("Electrical Repair", "ELC", 10),
    FAST_TALK("Fast Talk", "FTK", 5),
    FIGHTING_BRAWL("Fighting (Brawl)", "FIG", 25),
    FIREARMS_HANDGUN("Firearms (Handgun)", "HG", 20),
    FIREARMS_RIFLE_SHOTGUN("Firearms (Rifle/Shotgun)", "RFL", 25),
    FIRST_AID("First Aid", "FA", 30),
    HISTORY("History", "HIS", 5),
    INTIMIDATE("Intimidate", "INTM", 15),
    JUMP("Jump", "JMP", 20),
    LANGUAGE_OWN("Language (Own)", "LAN", 0),
    LAW("Law", "LAW", 5),
    LIBRARY_USE("Library Use", "LIB", 20),
    LISTEN("Listen", "LIS", 20),
    LOCKSMITH("Locksmith", "LCKS", 1),
    MECHANICAL_REPAIR("Mechanical Repair", "MCH", 10),
    MEDICINE("Medicine", "MED", 1),
    NATURAL_WORLD("Natural World", "NAT", 10),
    NAVIGATE("Navigate", "NAV", 10),
    OCCULT("Occult", "OCC", 5),
    PERSUADE("Persuade", "PRS", 10),
    PSYCHOANALYSIS("Psychoanalysis", "PSA", 1),
    PSYCHOLOGY("Psychology", "PSY", 10),
    RIDE("Ride", "RID", 5),
    SCIENCE("Science", "SCI", 1),
    SLEIGHT_OF_HAND("Sleight of Hand", "SLH", 10),
    SPOT_HIDDEN("Spot Hidden", "SPT", 25),
    STEALTH("Stealth", "STL", 20),
    SURVIVAL("Survival", "SRV", 10),
    SWIM("Swim", "SWM", 20),
    THROW("Throw", "THR", 20),
    TRACK("Track", "TRK", 10);

    private final String displayName;
    private final String code;
    private final int baseValue;

    CharacterSkill(String displayName, String code, int baseValue) {
        this.displayName = displayName;
        this.code = code;
        this.baseValue = baseValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public int getBaseValue() {
        return baseValue;
    }
}
