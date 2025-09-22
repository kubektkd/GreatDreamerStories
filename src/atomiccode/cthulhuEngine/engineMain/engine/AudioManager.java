package atomiccode.cthulhuEngine.engineMain.engine;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;
    private final Map<String, Clip> audioClips = new HashMap<>();
    private final Map<String, AudioInputStream> audioStreams = new HashMap<>();
    
    // Current playing music
    private String currentMusic = null;
    private Clip currentMusicClip = null;
    
    // Music transition settings
    private final float FADE_TIME = 2.0f; // seconds
    private boolean isTransitioning = false;
    private float transitionProgress = 0.0f;
    private float transitionStartVolume = 0.0f;
    private float transitionTargetVolume = 0.0f;
    
    private AudioManager() {}
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    public void loadAudio(String name, String path) {
        try {
            File audioFile = new File(path);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + path);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioStreams.put(name, audioStream);
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            audioClips.put(name, clip);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading audio file: " + path + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void playMusic(String name, float volume) {
        if (currentMusic != null && currentMusic.equals(name)) {
            return; // Already playing this music
        }
        
        // Stop current music if playing
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            stopMusic();
        }
        
        Clip clip = audioClips.get(name);
        if (clip != null) {
            currentMusic = name;
            currentMusicClip = clip;
            
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(name, volume);
        }
    }
    
    public void playMusicWithFade(String name, float volume) {
        if (currentMusic != null && currentMusic.equals(name)) {
            return; // Already playing this music
        }
        
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            // Start fade transition
            startFadeTransition(name, volume);
        } else {
            // No current music, just start new one
            playMusic(name, volume);
        }
    }
    
    private void startFadeTransition(String newMusicName, float targetVolume) {
        isTransitioning = true;
        transitionProgress = 0.0f;
        transitionTargetVolume = targetVolume;
        
        // Get current volume
        if (currentMusicClip != null) {
            FloatControl gainControl = (FloatControl) currentMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            transitionStartVolume = (float) Math.pow(10, gainControl.getValue() / 20.0);
        }
        
        // Load and prepare new music
        Clip newClip = audioClips.get(newMusicName);
        if (newClip != null) {
            if (newClip.isRunning()) {
                newClip.stop();
            }
            newClip.setFramePosition(0);
            newClip.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(newMusicName, 0.0f); // Start silent
        }
    }
    
    public void update(float deltaTime) {
        if (isTransitioning) {
            transitionProgress += deltaTime / FADE_TIME;
            
            if (transitionProgress >= 1.0f) {
                // Transition complete
                if (currentMusicClip != null) {
                    currentMusicClip.stop();
                }
                currentMusicClip = audioClips.get(currentMusic);
                setVolume(currentMusic, transitionTargetVolume);
                isTransitioning = false;
            } else {
                // Fade out old, fade in new
                if (currentMusicClip != null) {
                    float oldVolume = transitionStartVolume * (1.0f - transitionProgress);
                    setVolume(currentMusic, oldVolume);
                }
                
                // Fade in new music
                float newVolume = transitionTargetVolume * transitionProgress;
                setVolume(currentMusic, newVolume);
            }
        }
    }
    
    public void stopMusic() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
        currentMusic = null;
        currentMusicClip = null;
        isTransitioning = false;
    }
    
    public void setVolume(String name, float volume) {
        Clip clip = audioClips.get(name);
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(Math.max(0.001, volume)) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    public void playSound(String name) {
        Clip clip = audioClips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void cleanup() {
        stopMusic();
        for (Clip clip : audioClips.values()) {
            clip.close();
        }
        for (AudioInputStream stream : audioStreams.values()) {
            try {
                stream.close();
            } catch (IOException e) {
                System.err.println("Error closing audio stream: " + e.getMessage());
            }
        }
        audioClips.clear();
        audioStreams.clear();
    }
    
    public boolean isMusicPlaying() {
        return currentMusic != null && currentMusicClip != null && currentMusicClip.isRunning();
    }
    
    public String getCurrentMusic() {
        return currentMusic;
    }
}
