package atomiccode.greatDreamerStories.character;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a player character with stats, skills, and story progress.
 * Each character acts as a police chief and can be developed through gameplay.
 */
public class Character implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Character identity
    private String name;
    private Gender gender;
    private int age;
    private final String occupation = "Chief Police Officer"; // Fixed occupation
    
    // Character stats (total of 20 points to distribute)
    private int strength;      // Physical power and combat effectiveness
    private int power;         // Physical power and combat effectiveness
    private int education;     // Education and knowledge
    private int constitution;  // Constitution and health
    private int intelligence;  // Reasoning, memory, and problem-solving
    private int appearance;    // Physical appearance and attractiveness
    private int luck;          // Chance and good fortune
    private int size;          // Physical size and carrying capacity
    private int dexterity;     // Agility, reflexes, and precision

    /** Current pools (maxima follow CoC 7e from stats). */
    private int currentHitPoints;
    private int currentMagicPoints;
    private int currentSanity;

    // Character skills are derived from stats and fixed police occupation, not saved directly.
    private transient CharacterSkillSet skills;
    
    // Story progress
    private int currentStoryIndex;    // Which story the character is currently on
    private boolean[] completedStories; // Track which stories have been completed
    private int totalPlaytime;        // In minutes
    
    // Character creation and last played times
    private LocalDateTime createdAt;
    private LocalDateTime lastPlayedAt;
    
    // Total skill points available for distribution (at character creation)
    public static final int INITIAL_SKILL_POINTS = 500;
    public static final int INITIAL_STAT_VALUE = 50;
    public static final int MIN_STAT_VALUE = 15;
    public static final int MAX_STAT_VALUE = 90;
    public static final int MAX_STORIES = 10; // Room for future story expansions

    /** Investigator age in years (used for MOV modifiers in CoC 7e). */
    public static final int MIN_INVESTIGATOR_AGE = 15;
    public static final int MAX_INVESTIGATOR_AGE = 100;
    public static final int DEFAULT_INVESTIGATOR_AGE = 35;
    
    public enum Gender {
        MALE("Male"),
        FEMALE("Female");
        
        private final String displayName;
        
        Gender(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Constructor for creating a new character
     */
    public Character(String name, Gender gender, int strength, int power, int education, int constitution,
                    int intelligence, int appearance, int luck, int size, int dexterity, int age) {
        this.name = name;
        this.gender = gender;
        this.strength = strength;
        this.power = power;
        this.education = education;
        this.constitution = constitution;
        this.intelligence = intelligence;
        this.appearance = appearance;
        this.luck = luck;
        this.size = size;
        this.dexterity = dexterity;
        if (age < MIN_INVESTIGATOR_AGE || age > MAX_INVESTIGATOR_AGE) {
            throw new IllegalArgumentException("Age must be between " + MIN_INVESTIGATOR_AGE + " and " + MAX_INVESTIGATOR_AGE);
        }
        this.age = age;
        
        this.currentStoryIndex = 0;
        this.completedStories = new boolean[MAX_STORIES];
        this.totalPlaytime = 0;
        this.createdAt = LocalDateTime.now();
        this.lastPlayedAt = LocalDateTime.now();
        
        // Validate stat distribution
        if (!isValidStatDistribution()) {
            throw new IllegalArgumentException("Invalid stat distribution. Must total " + INITIAL_SKILL_POINTS + " points with each stat between " + MIN_STAT_VALUE + " and " + MAX_STAT_VALUE);
        }

        refillResourcePoolsToMaximum();
        recalculateSkills();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        normalizeAge();
        if (currentHitPoints == 0 && currentMagicPoints == 0 && currentSanity == 0
                && (getMaxHitPoints() > 0 || getMaxMagicPoints() > 0 || getMaxSanityPoints() > 0)) {
            refillResourcePoolsToMaximum();
        } else {
            clampResourcePoolsToLegal();
        }
        recalculateSkills();
    }

    private void normalizeAge() {
        if (age < MIN_INVESTIGATOR_AGE || age > MAX_INVESTIGATOR_AGE) {
            age = DEFAULT_INVESTIGATOR_AGE;
        }
    }

    private void refillResourcePoolsToMaximum() {
        currentHitPoints = getMaxHitPoints();
        currentMagicPoints = getMaxMagicPoints();
        currentSanity = getMaxSanityPoints();
    }

    private void clampResourcePoolsToLegal() {
        currentHitPoints = Math.min(Math.max(0, currentHitPoints), getMaxHitPoints());
        currentMagicPoints = Math.min(Math.max(0, currentMagicPoints), getMaxMagicPoints());
        currentSanity = Math.min(Math.max(0, currentSanity), getMaxSanityPoints());
    }
    
    /**
     * Validates that the character's stats are properly distributed
     */
    public boolean isValidStatDistribution() {
        int total = strength + power + education + constitution + intelligence + appearance + luck + size + dexterity;
        return total == INITIAL_SKILL_POINTS &&
               strength >= MIN_STAT_VALUE && strength <= MAX_STAT_VALUE &&
               power >= MIN_STAT_VALUE && power <= MAX_STAT_VALUE &&
               education >= MIN_STAT_VALUE && education <= MAX_STAT_VALUE &&
               constitution >= MIN_STAT_VALUE && constitution <= MAX_STAT_VALUE &&
               intelligence >= MIN_STAT_VALUE && intelligence <= MAX_STAT_VALUE &&
               appearance >= MIN_STAT_VALUE && appearance <= MAX_STAT_VALUE &&
               luck >= MIN_STAT_VALUE && luck <= MAX_STAT_VALUE &&
               size >= MIN_STAT_VALUE && size <= MAX_STAT_VALUE &&
               dexterity >= MIN_STAT_VALUE && dexterity <= MAX_STAT_VALUE;
    }
    
    /**
     * Marks a story as completed and advances character progress
     */
    public void completeStory(int storyIndex) {
        if (storyIndex >= 0 && storyIndex < MAX_STORIES) {
            completedStories[storyIndex] = true;
            if (storyIndex == currentStoryIndex) {
                currentStoryIndex = Math.min(currentStoryIndex + 1, MAX_STORIES - 1);
            }
        }
    }
    
    /**
     * Checks if a story is available to play (unlocked)
     */
    public boolean isStoryUnlocked(int storyIndex) {
        if (storyIndex == 0) return true; // First story is always unlocked
        return storyIndex <= currentStoryIndex && storyIndex < MAX_STORIES;
    }
    
    /**
     * Updates the last played time and adds to total playtime
     */
    public void updatePlaytime(int minutesPlayed) {
        this.lastPlayedAt = LocalDateTime.now();
        this.totalPlaytime += minutesPlayed;
    }
    
    /**
     * Gets a summary string for displaying character info
     */
    public String getCharacterSummary() {
        int completedCount = 0;
        for (boolean completed : completedStories) {
            if (completed) completedCount++;
        }
        
        return String.format("%s (%s)  Age %d\nSTR:%d POW:%d EDU:%d CON:%d INT:%d APP:%d LCK:%d SIZ:%d DEX:%d\nHP:%d/%d MP:%d/%d SAN:%d/%d MOV:%d DB:%s Build:%d Dodge:%d%%\n%s\nStories: %d/%d completed\nPlaytime: %dh %dm",
            name, gender.getDisplayName(), age,
            strength, power, education, constitution, intelligence, appearance, luck, size, dexterity,
            currentHitPoints, getMaxHitPoints(),
            currentMagicPoints, getMaxMagicPoints(),
            currentSanity, getMaxSanityPoints(),
            getMoveRate(), getDamageBonus(), getBuild(), getDodgeValue(),
            getKeySkillSummary(),
            completedCount, MAX_STORIES,
            totalPlaytime / 60, totalPlaytime % 60);
    }

    public void recalculateSkills() {
        this.skills = SkillCalculator.calculateForPoliceOfficer(this);
    }

    public CharacterSkillSet getSkills() {
        ensureSkills();
        return skills;
    }

    public int getSkillValue(CharacterSkill skill) {
        ensureSkills();
        return skills.getValue(skill);
    }

    public String getKeySkillSummary() {
        ensureSkills();
        return skills.formatSkills(CharacterSkill.LAW, CharacterSkill.SPOT_HIDDEN, CharacterSkill.PSYCHOLOGY, CharacterSkill.FIREARMS_HANDGUN);
    }

    public String getAllSkillsSummary() {
        ensureSkills();
        return skills.formatSkills(8, CharacterSkill.values());
    }

    public List<String> getAllSkillsSummaryLines(int skillsPerLine) {
        ensureSkills();
        return skills.formatSkillLines(skillsPerLine, CharacterSkill.values());
    }

    private void ensureSkills() {
        if (skills == null) {
            recalculateSkills();
        }
    }

    public int getMaxHitPoints() {
        return CthulhuDerivedStats.maxHitPoints(this);
    }

    public int getMaxMagicPoints() {
        return CthulhuDerivedStats.maxMagicPoints(this);
    }

    public int getMaxSanityPoints() {
        return CthulhuDerivedStats.maxSanityPoints(this);
    }

    public int getMoveRate() {
        return CthulhuDerivedStats.moveRate(this);
    }

    /** CoC 7e damage bonus from STR + SIZ (e.g. "-1", "0", "+1d4"). */
    public String getDamageBonus() {
        return CthulhuDerivedStats.damageBonus(this);
    }

    public int getBuild() {
        return CthulhuDerivedStats.build(this);
    }

    /** Dodge skill rating (percent), including occupation and attribute modifiers. */
    public int getDodgeValue() {
        return getSkillValue(CharacterSkill.DODGE);
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public String getOccupation() { return occupation; }
    
    public int getAge() { return age; }
    public void setAge(int age) {
        if (age < MIN_INVESTIGATOR_AGE || age > MAX_INVESTIGATOR_AGE) {
            throw new IllegalArgumentException("Age must be between " + MIN_INVESTIGATOR_AGE + " and " + MAX_INVESTIGATOR_AGE);
        }
        this.age = age;
    }

    public int getCurrentHitPoints() { return currentHitPoints; }
    public void setCurrentHitPoints(int currentHitPoints) {
        this.currentHitPoints = Math.min(Math.max(0, currentHitPoints), getMaxHitPoints());
    }

    public int getCurrentMagicPoints() { return currentMagicPoints; }
    public void setCurrentMagicPoints(int currentMagicPoints) {
        this.currentMagicPoints = Math.min(Math.max(0, currentMagicPoints), getMaxMagicPoints());
    }

    public int getCurrentSanity() { return currentSanity; }
    public void setCurrentSanity(int currentSanity) {
        this.currentSanity = Math.min(Math.max(0, currentSanity), getMaxSanityPoints());
    }

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; clampResourcePoolsToLegal(); recalculateSkills(); }
    
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; clampResourcePoolsToLegal(); recalculateSkills(); }
    
    public int getEducation() { return education; }
    public void setEducation(int education) { this.education = education; recalculateSkills(); }
    
    public int getConstitution() { return constitution; }
    public void setConstitution(int constitution) { this.constitution = constitution; clampResourcePoolsToLegal(); recalculateSkills(); }
    
    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; recalculateSkills(); }
    
    public int getAppearance() { return appearance; }
    public void setAppearance(int appearance) { this.appearance = appearance; recalculateSkills(); }
    
    public int getLuck() { return luck; }
    public void setLuck(int luck) { this.luck = luck; recalculateSkills(); }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; clampResourcePoolsToLegal(); recalculateSkills(); }

    public int getDexterity() { return dexterity; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; recalculateSkills(); }
    
    public int getCurrentStoryIndex() { return currentStoryIndex; }
    public void setCurrentStoryIndex(int currentStoryIndex) { this.currentStoryIndex = currentStoryIndex; }
    
    public boolean[] getCompletedStories() { return completedStories.clone(); }
    public boolean isStoryCompleted(int storyIndex) { 
        return storyIndex >= 0 && storyIndex < MAX_STORIES && completedStories[storyIndex]; 
    }

    public int getCompletedStoryCount() {
        int count = 0;
        for (boolean completed : completedStories) {
            if (completed) count++;
        }
        return count;
    }
    
    public int getTotalPlaytime() { return totalPlaytime; }
    public void setTotalPlaytime(int totalPlaytime) { this.totalPlaytime = totalPlaytime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastPlayedAt() { return lastPlayedAt; }
    public void setLastPlayedAt(LocalDateTime lastPlayedAt) { this.lastPlayedAt = lastPlayedAt; }
}


