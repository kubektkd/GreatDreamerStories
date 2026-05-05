package atomiccode.greatDreamerStories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

/**
 * Persisted player preferences (LibGDX {@link Preferences}), shared across sessions.
 */
public final class GamePreferences {

    private static final String STORE = "great-dreamer-stories";
    private static final String KEY_MENU_MUSIC_VOLUME = "menuMusicVolume";
    private static final String KEY_SFX_VOLUME = "sfxVolume";
    /** Replaced by {@link #KEY_WINDOW_MODE}; read once for migration. */
    private static final String KEY_FULLSCREEN_LEGACY = "fullscreen";
    private static final String KEY_WINDOW_MODE = "windowMode";

    private static final String KEY_DIFFICULTY = "difficulty";

    public static final float DEFAULT_MENU_MUSIC_VOLUME = 0.3f;
    public static final float DEFAULT_SFX_VOLUME = 0.85f;

    public static final GameDifficulty DEFAULT_DIFFICULTY = GameDifficulty.NORMAL;

    private GamePreferences() {
    }

    private static Preferences prefs() {
        return Gdx.app.getPreferences(STORE);
    }

    public static float getMenuMusicVolume() {
        return prefs().getFloat(KEY_MENU_MUSIC_VOLUME, DEFAULT_MENU_MUSIC_VOLUME);
    }

    public static void setMenuMusicVolume(float volume) {
        float v = Math.min(1f, Math.max(0f, volume));
        Preferences p = prefs();
        p.putFloat(KEY_MENU_MUSIC_VOLUME, v);
        p.flush();
    }

    public static float getSfxVolume() {
        return prefs().getFloat(KEY_SFX_VOLUME, DEFAULT_SFX_VOLUME);
    }

    public static void setSfxVolume(float volume) {
        float v = Math.min(1f, Math.max(0f, volume));
        Preferences p = prefs();
        p.putFloat(KEY_SFX_VOLUME, v);
        p.flush();
    }

    public static Window.Mode getWindowMode() {
        Preferences p = prefs();
        if (!p.contains(KEY_WINDOW_MODE)) {
            Window.Mode mode = p.getBoolean(KEY_FULLSCREEN_LEGACY, false)
                    ? Window.Mode.FULLSCREEN
                    : Window.Mode.WINDOWED;
            putWindowMode(p, mode);
            return mode;
        }
        try {
            return Window.Mode.valueOf(p.getString(KEY_WINDOW_MODE, Window.Mode.WINDOWED.name()));
        } catch (IllegalArgumentException ex) {
            return Window.Mode.WINDOWED;
        }
    }

    public static void setWindowMode(Window.Mode mode) {
        Preferences p = prefs();
        putWindowMode(p, mode);
    }

    private static void putWindowMode(Preferences p, Window.Mode mode) {
        p.putString(KEY_WINDOW_MODE, mode.name());
        p.flush();
    }

    public static GameDifficulty getDifficulty() {
        Preferences p = prefs();
        try {
            String name = p.getString(KEY_DIFFICULTY, DEFAULT_DIFFICULTY.name());
            return GameDifficulty.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return DEFAULT_DIFFICULTY;
        }
    }

    public static void setDifficulty(GameDifficulty difficulty) {
        Preferences pref = prefs();
        pref.putString(KEY_DIFFICULTY,
                difficulty != null ? difficulty.name() : DEFAULT_DIFFICULTY.name());
        pref.flush();
    }
}
