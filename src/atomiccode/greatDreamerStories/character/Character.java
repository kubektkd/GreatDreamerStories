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
    private int dexterity;     // Agility, reflexes, and precision
    private int intelligence;  // Reasoning, memory, and problem-solving
    private int perception;    // Awareness, intuition, and observation
    private int charisma;      // Leadership, persuasion, and social skills
    
    // Story progress
    private int currentStoryIndex;    // Which story the character is currently on
    private boolean[] completedStories; // Track which stories have been completed
    private int totalPlaytime;        // In minutes
    
    // Character creation and last played times
    private LocalDateTime createdAt;
    private LocalDateTime lastPlayedAt;
    
    // Total skill points available for distribution (at character creation)
    public static final int INITIAL_SKILL_POINTS = 20;
    public static final int MIN_STAT_VALUE = 1;
    public static final int MAX_STAT_VALUE = 10;
    public static final int MAX_STORIES = 10; // Room for future story expansions
    
    public enum Gender {
        MALE("Male"),
        FEMALE("Female"),
        OTHER("Other");
        
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
    public Character(String name, Gender gender, int strength, int dexterity, 
                    int intelligence, int perception, int charisma) {
        this.name = name;
        this.gender = gender;
        this.strength = strength;
        this.dexterity = dexterity;
        this.intelligence = intelligence;
        this.perception = perception;
        this.charisma = charisma;
        
        this.currentStoryIndex = 0;
        this.completedStories = new boolean[MAX_STORIES];
        this.totalPlaytime = 0;
        this.createdAt = LocalDateTime.now();
        this.lastPlayedAt = LocalDateTime.now();
        
        // Validate stat distribution
        if (!isValidStatDistribution()) {
            throw new IllegalArgumentException("Invalid stat distribution. Must total " + INITIAL_SKILL_POINTS + " points with each stat between " + MIN_STAT_VALUE + " and " + MAX_STAT_VALUE);
        }
    }
    
    /**
     * Validates that the character's stats are properly distributed
     */
    public boolean isValidStatDistribution() {
        int total = strength + dexterity + intelligence + perception + charisma;
        return total == INITIAL_SKILL_POINTS &&
               strength >= MIN_STAT_VALUE && strength <= MAX_STAT_VALUE &&
               dexterity >= MIN_STAT_VALUE && dexterity <= MAX_STAT_VALUE &&
               intelligence >= MIN_STAT_VALUE && intelligence <= MAX_STAT_VALUE &&
               perception >= MIN_STAT_VALUE && perception <= MAX_STAT_VALUE &&
               charisma >= MIN_STAT_VALUE && charisma <= MAX_STAT_VALUE;
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
        
        return String.format("%s (%s)\nSTR:%d DEX:%d INT:%d PER:%d CHA:%d\nStories: %d/%d completed\nPlaytime: %dh %dm", 
            name, gender.getDisplayName(),
            strength, dexterity, intelligence, perception, charisma,
            completedCount, MAX_STORIES,
            totalPlaytime / 60, totalPlaytime % 60);
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public String getOccupation() { return occupation; }
    
    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }
    
    public int getDexterity() { return dexterity; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; }
    
    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }
    
    public int getPerception() { return perception; }
    public void setPerception(int perception) { this.perception = perception; }
    
    public int getCharisma() { return charisma; }
    public void setCharisma(int charisma) { this.charisma = charisma; }
    
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


