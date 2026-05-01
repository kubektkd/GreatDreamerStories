package atomiccode.greatDreamerStories.character;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a player character with stats, skills, and story progress.
 * Each character acts as a police chief and can be developed through gameplay.
 */
public class Character implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Character identity
    private String name;
    private Gender gender;
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
                    int intelligence, int appearance, int luck, int size, int dexterity) {
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
        
        this.currentStoryIndex = 0;
        this.completedStories = new boolean[MAX_STORIES];
        this.totalPlaytime = 0;
        this.createdAt = LocalDateTime.now();
        this.lastPlayedAt = LocalDateTime.now();
        
        // Validate stat distribution
        if (!isValidStatDistribution()) {
            throw new IllegalArgumentException("Invalid stat distribution. Must total " + INITIAL_SKILL_POINTS + " points with each stat between " + MIN_STAT_VALUE + " and " + MAX_STAT_VALUE);
        }

        recalculateSkills();
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
        
        return String.format("%s (%s)\nSTR:%d POW:%d EDU:%d CON:%d INT:%d APP:%d LCK:%d SIZ:%d DEX:%d\n%s\nStories: %d/%d completed\nPlaytime: %dh %dm",
            name, gender.getDisplayName(),
            strength, power, education, constitution, intelligence, appearance, luck, size, dexterity,
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

    private void ensureSkills() {
        if (skills == null) {
            recalculateSkills();
        }
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public String getOccupation() { return occupation; }
    
    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; recalculateSkills(); }
    
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; recalculateSkills(); }
    
    public int getEducation() { return education; }
    public void setEducation(int education) { this.education = education; recalculateSkills(); }
    
    public int getConstitution() { return constitution; }
    public void setConstitution(int constitution) { this.constitution = constitution; recalculateSkills(); }
    
    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; recalculateSkills(); }
    
    public int getAppearance() { return appearance; }
    public void setAppearance(int appearance) { this.appearance = appearance; recalculateSkills(); }
    
    public int getLuck() { return luck; }
    public void setLuck(int luck) { this.luck = luck; recalculateSkills(); }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; recalculateSkills(); }

    public int getDexterity() { return dexterity; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; recalculateSkills(); }
    
    public int getCurrentStoryIndex() { return currentStoryIndex; }
    public void setCurrentStoryIndex(int currentStoryIndex) { this.currentStoryIndex = currentStoryIndex; }
    
    public boolean[] getCompletedStories() { return completedStories.clone(); }
    public boolean isStoryCompleted(int storyIndex) { 
        return storyIndex >= 0 && storyIndex < MAX_STORIES && completedStories[storyIndex]; 
    }
    
    public int getTotalPlaytime() { return totalPlaytime; }
    public void setTotalPlaytime(int totalPlaytime) { this.totalPlaytime = totalPlaytime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastPlayedAt() { return lastPlayedAt; }
    public void setLastPlayedAt(LocalDateTime lastPlayedAt) { this.lastPlayedAt = lastPlayedAt; }
}


