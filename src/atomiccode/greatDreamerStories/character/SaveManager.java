package atomiccode.greatDreamerStories.character;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages character save files and loading.
 * Supports up to 8 character slots with persistent storage.
 */
public class SaveManager {
    private static final String SAVES_DIRECTORY = "saves";
    private static final String CHARACTER_FILE_PREFIX = "character_";
    private static final String CHARACTER_FILE_EXTENSION = ".dat";
    private static final int MAX_CHARACTER_SLOTS = 8;
    
    private static SaveManager instance;
    private List<Character> loadedCharacters;
    
    private SaveManager() {
        this.loadedCharacters = new ArrayList<>();
        initializeSaveDirectory();
        loadAllCharacters();
    }
    
    /**
     * Get singleton instance of SaveManager
     */
    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }
    
    /**
     * Initialize the saves directory if it doesn't exist
     */
    private void initializeSaveDirectory() {
        try {
            Path savesPath = Paths.get(SAVES_DIRECTORY);
            if (!Files.exists(savesPath)) {
                Files.createDirectories(savesPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to create saves directory: " + e.getMessage());
        }
    }
    
    /**
     * Load all characters from save files
     */
    private void loadAllCharacters() {
        loadedCharacters.clear();
        
        // Initialize with null values for all slots
        for (int i = 0; i < MAX_CHARACTER_SLOTS; i++) {
            loadedCharacters.add(null);
        }
        
        // Load existing characters
        for (int slot = 0; slot < MAX_CHARACTER_SLOTS; slot++) {
            Character character = loadCharacterFromSlot(slot);
            if (character != null) {
                loadedCharacters.set(slot, character);
            }
        }
    }
    
    /**
     * Load a character from a specific slot
     */
    private Character loadCharacterFromSlot(int slot) {
        if (slot < 0 || slot >= MAX_CHARACTER_SLOTS) {
            return null;
        }
        
        String filename = CHARACTER_FILE_PREFIX + slot + CHARACTER_FILE_EXTENSION;
        Path filePath = Paths.get(SAVES_DIRECTORY, filename);
        
        if (!Files.exists(filePath)) {
            return null;
        }
        
        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            Character character = (Character) ois.readObject();
            character.recalculateSkills();
            return character;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load character from slot " + slot + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Save a character to a specific slot
     */
    public boolean saveCharacter(Character character, int slot) {
        if (slot < 0 || slot >= MAX_CHARACTER_SLOTS || character == null) {
            return false;
        }
        
        String filename = CHARACTER_FILE_PREFIX + slot + CHARACTER_FILE_EXTENSION;
        Path filePath = Paths.get(SAVES_DIRECTORY, filename);
        
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            oos.writeObject(character);
            loadedCharacters.set(slot, character);
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to save character to slot " + slot + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a character from a specific slot
     */
    public boolean deleteCharacter(int slot) {
        if (slot < 0 || slot >= MAX_CHARACTER_SLOTS) {
            return false;
        }
        
        String filename = CHARACTER_FILE_PREFIX + slot + CHARACTER_FILE_EXTENSION;
        Path filePath = Paths.get(SAVES_DIRECTORY, filename);
        
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            loadedCharacters.set(slot, null);
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to delete character from slot " + slot + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get character from a specific slot
     */
    public Character getCharacter(int slot) {
        if (slot < 0 || slot >= MAX_CHARACTER_SLOTS) {
            return null;
        }
        return loadedCharacters.get(slot);
    }
    
    /**
     * Check if a slot is occupied
     */
    public boolean isSlotOccupied(int slot) {
        return getCharacter(slot) != null;
    }
    
    /**
     * Get the first available (empty) slot
     */
    public int getFirstAvailableSlot() {
        for (int i = 0; i < MAX_CHARACTER_SLOTS; i++) {
            if (!isSlotOccupied(i)) {
                return i;
            }
        }
        return -1; // No available slots
    }
    
    /**
     * Get all loaded characters
     */
    public List<Character> getAllCharacters() {
        return new ArrayList<>(loadedCharacters);
    }
    
    /**
     * Get the maximum number of character slots
     */
    public int getMaxSlots() {
        return MAX_CHARACTER_SLOTS;
    }
    
    /**
     * Check if all slots are occupied
     */
    public boolean isAllSlotsFull() {
        return getFirstAvailableSlot() == -1;
    }
    
    /**
     * Get the number of occupied slots
     */
    public int getOccupiedSlotCount() {
        int count = 0;
        for (Character character : loadedCharacters) {
            if (character != null) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Reload all characters from disk (useful after external changes)
     */
    public void refreshCharacters() {
        loadAllCharacters();
    }
    
    /**
     * Update an existing character's data and save it
     */
    public boolean updateCharacter(Character character, int slot) {
        if (slot < 0 || slot >= MAX_CHARACTER_SLOTS || character == null) {
            return false;
        }
        
        if (!isSlotOccupied(slot)) {
            return false; // Slot must already contain a character to update
        }
        
        return saveCharacter(character, slot);
    }
}


