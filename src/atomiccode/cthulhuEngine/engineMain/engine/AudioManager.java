package atomiccode.cthulhuEngine.engineMain.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;

    private static final float FADE_TIME = 2.0f;

    private final Map<String, Music> musicTracks = new HashMap<>();
    private final Map<String, Sound> sounds = new HashMap<>();

    private Music currentMusic;
    private String currentMusicName;

    /** Multiplier applied to every {@link #playSound(String, float)} call (linear 0–1). */
    private float masterSfxVolume = 1f;

    private boolean isTransitioning;
    private float transitionProgress;
    private float transitionStartVolume;
    private float transitionTargetVolume;

    private AudioManager() {
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void loadAudio(String name, String path) {
        FileHandle file = resolveFile(path);
        if (!file.exists()) {
            System.err.println("Audio file not found: " + path);
            return;
        }

        String extension = file.extension().toLowerCase();
        if ("ogg".equals(extension) || "mp3".equals(extension)) {
            musicTracks.computeIfAbsent(name, ignored -> Gdx.audio.newMusic(file));
        } else if ("wav".equals(extension)) {
            sounds.computeIfAbsent(name, ignored -> Gdx.audio.newSound(file));
        } else {
            System.err.println("Unsupported audio format: " + extension);
        }
    }

    public void playMusicWithFade(String name, float targetVolume) {
        Music music = musicTracks.get(name);
        if (music == null) {
            System.err.println("Music not loaded: " + name);
            return;
        }

        // Same track as current: never stop/play again (fixes gaps when revisiting menus).
        if (currentMusic == music && name.equals(currentMusicName)) {
            transitionStartVolume = music.getVolume();
            transitionTargetVolume = targetVolume;
            transitionProgress = 0f;
            isTransitioning = Math.abs(transitionTargetVolume - transitionStartVolume) > 0.01f;

            music.setLooping(true);
            if (!music.isPlaying()) {
                music.play();
            }
            return;
        }

        if (currentMusic != null && currentMusic != music) {
            currentMusic.stop();
        }

        currentMusic = music;
        currentMusicName = name;
        transitionStartVolume = 0f;
        transitionTargetVolume = targetVolume;
        transitionProgress = 0f;
        isTransitioning = true;

        currentMusic.setLooping(true);
        currentMusic.setVolume(0f);
        currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = null;
        currentMusicName = null;
        isTransitioning = false;
    }

    public void update(float deltaTime) {
        if (!isTransitioning || currentMusic == null) {
            return;
        }

        transitionProgress += deltaTime / FADE_TIME;
        if (transitionProgress >= 1f) {
            transitionProgress = 1f;
            isTransitioning = false;
        }

        float volume = transitionStartVolume + (transitionTargetVolume - transitionStartVolume) * transitionProgress;
        currentMusic.setVolume(volume);
    }

    public void setVolume(String name, float volume) {
        Music music = musicTracks.get(name);
        if (music != null) {
            music.setVolume(volume);
        }
    }

    public float getMusicVolume(String name) {
        Music music = musicTracks.get(name);
        return music != null ? music.getVolume() : 0f;
    }

    /**
     * @param volume Linear gain for this sound alone (combined with master SFX multiplier).
     */
    public void playSound(String name, float volume) {
        Sound sound = sounds.get(name);
        if (sound != null) {
            sound.play(volume * masterSfxVolume);
        }
    }

    public void setMasterSfxVolume(float volume) {
        masterSfxVolume = Math.min(1f, Math.max(0f, volume));
    }

    public float getMasterSfxVolume() {
        return masterSfxVolume;
    }

    public void cleanup() {
        stopMusic();
        for (Music music : musicTracks.values()) {
            music.dispose();
        }
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        musicTracks.clear();
        sounds.clear();
    }

    public String getCurrentMusicName() {
        return currentMusicName;
    }

    private FileHandle resolveFile(String path) {
        File absolute = new File(path);
        if (absolute.isAbsolute()) {
            return Gdx.files.absolute(path);
        }
        return Gdx.files.internal(path);
    }
}
