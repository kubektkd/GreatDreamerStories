package atomiccode.cthulhuEngine.engineMain.engine;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

// LWJGL imports for OpenAL and stb_vorbis
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;

public class AudioManager {
    private static AudioManager instance;
    
    // OpenAL context and device
    private long device;
    private long context;
    
    // Audio storage
    private final Map<String, Integer> audioBuffers = new HashMap<>();
    private final Map<String, Integer> audioSources = new HashMap<>();
    private final Map<String, AudioData> audioData = new HashMap<>();
    
    // Java Sound for WAV files (fallback)
    private final Map<String, Clip> audioClips = new HashMap<>();
    private final Map<String, AudioInputStream> audioStreams = new HashMap<>();
    
    // Current playing music
    private String currentMusic = null;
    private Integer currentMusicSource = null;
    private Clip currentMusicClip = null;
    
    // Music transition settings
    private final float FADE_TIME = 2.0f; // seconds
    private boolean isTransitioning = false;
    private float transitionProgress = 0.0f;
    private float transitionStartVolume = 0.0f;
    private float transitionTargetVolume = 0.0f;
    
    // Audio data structure
    private static class AudioData {
        public final int channels;
        public final int sampleRate;
        public final ShortBuffer pcm;
        
        public AudioData(int channels, int sampleRate, ShortBuffer pcm) {
            this.channels = channels;
            this.sampleRate = sampleRate;
            this.pcm = pcm;
        }
        
        public int getChannels() {
            return channels;
        }
        
        public int getSampleRate() {
            return sampleRate;
        }
        
        public ShortBuffer getPcm() {
            return pcm;
        }
    }
    
    private AudioManager() {
        initializeOpenAL();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    private void initializeOpenAL() {
        try {
            // Initialize OpenAL
            device = alcOpenDevice((ByteBuffer) null);
            if (device == MemoryUtil.NULL) {
                throw new RuntimeException("Failed to open OpenAL device");
            }
            
            context = alcCreateContext(device, (IntBuffer) null);
            if (context == MemoryUtil.NULL) {
                throw new RuntimeException("Failed to create OpenAL context");
            }
            
            alcMakeContextCurrent(context);
            AL.createCapabilities(ALC.createCapabilities(device));
            
            
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize OpenAL: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void loadAudio(String name, String path) {
        File audioFile = new File(path);
        if (!audioFile.exists()) {
            System.err.println("Audio file not found: " + path);
            return;
        }
        
        String extension = getFileExtension(path).toLowerCase();
        
        // Handle different audio formats
        switch (extension) {
            case "wav":
            case "aiff":
            case "aif":
            case "au":
                loadJavaSoundAudio(name, path);
                break;
            case "ogg":
                loadOGGAudio(name, path);
                break;
            case "mp3":
                System.err.println("MP3 support not implemented yet. Please use OGG format.");
                break;
            default:
                System.err.println("Unsupported audio format: " + extension);
        }
    }
    
    private void loadJavaSoundAudio(String name, String path) {
        try {
            File audioFile = new File(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioStreams.put(name, audioStream);
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            audioClips.put(name, clip);
            
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading Java Sound audio file: " + path + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadOGGAudio(String name, String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_filename(path, error, null);
            
            if (decoder == MemoryUtil.NULL) {
                System.err.println("Failed to open OGG file: " + path + " - Error: " + error.get(0));
                return;
            }
            
            STBVorbisInfo info = STBVorbisInfo.malloc();
            stb_vorbis_get_info(decoder, info);
            
            int channels = info.channels();
            int sampleRate = info.sample_rate();
            int samples = (int) stb_vorbis_stream_length_in_samples(decoder);
            
            ShortBuffer pcm = MemoryUtil.memAllocShort(samples * channels);
            stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
            stb_vorbis_close(decoder);
            
            // Store audio data
            audioData.put(name, new AudioData(channels, sampleRate, pcm));
            
            // Create OpenAL buffer
            int bufferId = alGenBuffers();
            int format = channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
            alBufferData(bufferId, format, pcm, sampleRate);
            audioBuffers.put(name, bufferId);
            
            // Create OpenAL source
            int sourceId = alGenSources();
            alSourcei(sourceId, AL_BUFFER, bufferId);
            alSourcei(sourceId, AL_LOOPING, AL_TRUE);
            audioSources.put(name, sourceId);
            
            
        } catch (Exception e) {
            System.err.println("❌ Error loading OGG audio file: " + path + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return path.substring(lastDotIndex + 1);
    }
    
    public void playMusic(String name, float volume) {
        if (currentMusic != null && currentMusic.equals(name)) {
            return; // Already playing this music
        }
        
        // Stop current music if playing
        if (currentMusicSource != null) {
            alSourceStop(currentMusicSource);
        }
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
        
        // Try OpenAL first (OGG files)
        Integer sourceId = audioSources.get(name);
        if (sourceId != null) {
            currentMusic = name;
            currentMusicSource = sourceId;
            currentMusicClip = null; // Clear Java Sound clip
            
            alSourcePlay(sourceId);
            setVolume(name, volume);
            return;
        }
        
        // Try Java Sound (WAV, AIFF, AU)
        Clip clip = audioClips.get(name);
        if (clip != null) {
            currentMusic = name;
            currentMusicClip = clip;
            currentMusicSource = null; // Clear OpenAL source
            
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
        
        if (currentMusicSource != null || (currentMusicClip != null && currentMusicClip.isRunning())) {
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
        if (currentMusicSource != null) {
            // For OpenAL sources, we'll use a simple linear fade
            transitionStartVolume = 1.0f; // Assume full volume
        } else if (currentMusicClip != null) {
            FloatControl gainControl = (FloatControl) currentMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            transitionStartVolume = (float) Math.pow(10, gainControl.getValue() / 20.0);
        }
        
        // Load and prepare new music
        playMusic(newMusicName, 0.0f); // Start silent
    }
    
    public void update(float deltaTime) {
        if (isTransitioning) {
            transitionProgress += deltaTime / FADE_TIME;
            
            if (transitionProgress >= 1.0f) {
                // Transition complete
                if (currentMusicSource != null) {
                    alSourceStop(currentMusicSource);
                }
                if (currentMusicClip != null) {
                    currentMusicClip.stop();
                }
                
                // Switch to new music
                playMusic(currentMusic, transitionTargetVolume);
                isTransitioning = false;
            } else {
                // Fade out old, fade in new
                if (currentMusicSource != null) {
                    float oldVolume = transitionStartVolume * (1.0f - transitionProgress);
                    setVolume(currentMusic, oldVolume);
                } else if (currentMusicClip != null) {
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
        if (currentMusicSource != null) {
            alSourceStop(currentMusicSource);
        }
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
        currentMusic = null;
        currentMusicSource = null;
        currentMusicClip = null;
        isTransitioning = false;
    }
    
    public void setVolume(String name, float volume) {
        // Try OpenAL first (OGG files)
        Integer sourceId = audioSources.get(name);
        if (sourceId != null) {
            alSourcef(sourceId, AL_GAIN, Math.max(0.0f, Math.min(1.0f, volume)));
            return;
        }
        
        // Try Java Sound (WAV, AIFF, AU)
        Clip clip = audioClips.get(name);
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(Math.max(0.001, volume)) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    public void playSound(String name) {
        // Try OpenAL first (OGG files)
        Integer sourceId = audioSources.get(name);
        if (sourceId != null) {
            alSourcePlay(sourceId);
            return;
        }
        
        // Try Java Sound (WAV, AIFF, AU)
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
        
        // Cleanup OpenAL resources
        for (Integer sourceId : audioSources.values()) {
            alDeleteSources(sourceId);
        }
        for (Integer bufferId : audioBuffers.values()) {
            alDeleteBuffers(bufferId);
        }
        for (AudioData data : audioData.values()) {
            MemoryUtil.memFree(data.getPcm());
        }
        
        audioSources.clear();
        audioBuffers.clear();
        audioData.clear();
        
        // Cleanup Java Sound resources
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
        
        // Cleanup OpenAL context
        if (context != MemoryUtil.NULL) {
            alcDestroyContext(context);
        }
        if (device != MemoryUtil.NULL) {
            alcCloseDevice(device);
        }
    }
    
    public boolean isMusicPlaying() {
        if (currentMusicSource != null) {
            return alGetSourcei(currentMusicSource, AL_SOURCE_STATE) == AL_PLAYING;
        }
        if (currentMusicClip != null) {
            return currentMusicClip.isRunning();
        }
        return false;
    }
    
    public String getCurrentMusic() {
        return currentMusic;
    }
}