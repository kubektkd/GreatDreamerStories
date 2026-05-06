package atomiccode.greatDreamerStories.i18n;

import com.badlogic.gdx.Gdx;

import atomiccode.cthulhuEngine.engineMain.engine.EngineFiles;
import atomiccode.greatDreamerStories.GamePreferences;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Properties;

/**
 * Loads UTF-8 {@code .properties} from {@code res/i18n/locale_xx.properties}.
 * English ({@code en}) is always loaded; other locales overlay keys. Missing values show {@code [key]} for QA.
 * <p>
 * Key naming (flat, dotted): {@code <area>.<context>.<element>} — e.g. {@code settings.panel.audio},
 * {@code screen.character_select.title}, {@code stat.str.full}, {@code skill.fast_talk},
 * {@code skill.fast_talk.code} (short sheet codes; used for sorting in the dossier table).
 * Story-specific lines: {@code story01.gameplay.line01} (expand per DLC).
 */
public final class GameTexts {

    private static Properties english = new Properties();
    private static Properties overlay = new Properties();
    private static String activeTag = "en";

    private GameTexts() {
    }

    public static void init() {
        File dir = new File(EngineFiles.RES_FOLDER, "i18n");
        english = loadLocaleFile(new File(dir, "locale_en.properties"));
        setLocaleForTag(GamePreferences.getLanguageTag());
    }

    /**
     * ISO tag: {@code en}, {@code pl}. Unknown tags fall back to {@code en}.
     */
    public static void setLocaleForTag(String languageTag) {
        activeTag = normalizeTag(languageTag);
        overlay = new Properties();
        if (!"en".equals(activeTag)) {
            File dir = new File(EngineFiles.RES_FOLDER, "i18n");
            overlay = loadLocaleFile(new File(dir, "locale_" + activeTag + ".properties"));
        }
    }

    public static String getActiveLanguageTag() {
        return activeTag;
    }

    public static Locale getActiveLocale() {
        return Locale.forLanguageTag(activeTag);
    }

    public static String tr(String key) {
        if (key == null) {
            return "";
        }
        String v = overlay.getProperty(key);
        if (isPresent(v)) {
            return v;
        }
        v = english.getProperty(key);
        if (isPresent(v)) {
            return v;
        }
        return "[" + key + "]";
    }

    /**
     * Uses {@link String#format(Locale, String, Object...)} with the active locale for correct grouping separators etc.
     */
    public static String trf(String key, Object... args) {
        return String.format(getActiveLocale(), tr(key), args);
    }

    /**
     * Full archive header line: shared {@code archive.strapline_prefix} plus a location-specific suffix
     * (e.g. {@code archive.strapline.case_files}).
     */
    public static String archiveStrapline(String straplineSuffixKey) {
        return tr("archive.strapline_prefix") + tr(straplineSuffixKey);
    }

    private static boolean isPresent(String v) {
        return v != null && !v.isEmpty();
    }

    private static String normalizeTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return "en";
        }
        String t = tag.trim().toLowerCase(Locale.ROOT);
        if (!t.equals("pl")) {
            return "en";
        }
        return t;
    }

    private static Properties loadLocaleFile(File file) {
        Properties p = new Properties();
        if (!file.exists()) {
            Gdx.app.error("GameTexts", "Missing locale file: " + file.getAbsolutePath());
            return p;
        }
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()));
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            p.load(reader);
        } catch (IOException e) {
            Gdx.app.error("GameTexts", "Failed to load " + file.getAbsolutePath(), e);
        }
        return p;
    }
}
