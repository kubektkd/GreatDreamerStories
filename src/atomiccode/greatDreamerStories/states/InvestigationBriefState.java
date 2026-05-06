package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.Tooltip;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.character.SaveManager;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GeneralMenuLayout;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;
import atomiccode.greatDreamerStories.character.CharacterSkill;
import atomiccode.greatDreamerStories.ui.menu.MenuActionStrip;
import atomiccode.greatDreamerStories.ui.menu.MenuChipPanel;
import atomiccode.greatDreamerStories.ui.menu.MenuScreenTitle;
import atomiccode.greatDreamerStories.ui.menu.MenuTable;
import atomiccode.greatDreamerStories.i18n.GameTexts;

import com.badlogic.gdx.graphics.Cursor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Briefing state that summarizes investigator context before launching gameplay.
 * It shows current case data, recent notes, and acts as a staging point for the actual mission.
 */
public class InvestigationBriefState implements State {

    /** Leading graphic for back navigation (not translated). */
    private static final String BACK_BUTTON_GRAPHIC_PREFIX = "<  ";
    private static final int BACK_BUTTON_WIDTH = 130;
    private static final int START_BUTTON_WIDTH = 170;
    private static final int ACTION_BUTTON_HEIGHT = 48;
    private static final int ACTION_BUTTON_GAP = 16;

    private final Character selectedCharacter;
    private final int characterSlot;
    
    private Button backButton;
    private Button startButton;
    private Button[] menuButtons;
    private int selectedIndex = 0;
    private GeneralMenuLayout layout;
    private UiRect dossierPanel;
    private UiRect storyPanel;
    private UiRect notesPanel;
    private MenuChipPanel dossierChipPanel;
    private MenuChipPanel storyChipPanel;
    private MenuChipPanel notesChipPanel;
    
    // Display info
    private Font textFont;
    private Font smallFont;
    private Font buttonFont;

    private static final int STAT_GRID_ROWS = 4;
    private static final int STAT_GRID_COLS = 4;
    private static final int SKILL_TABLE_COLS = 6;
    private static final int SKILLS_PER_TABLE_ROW = 3;
    private static final long SKILL_TABLE_TOOLTIP_DELAY_MS = 300;

    private String[][] skillTableCells;
    private String[][] skillTableTooltips;
    private String[][] statSummaryCells;
    private String[][] statSummaryTooltips;
    private Tooltip skillTableTooltip;
    
    public InvestigationBriefState(Character character, int characterSlot) {
        this.selectedCharacter = character;
        this.characterSlot = characterSlot;
    }
    
    @Override
    public int getPriority() {
        return 0;
    }
    
    @Override
    public void onEnter() {
        // Initialize fonts
        textFont = GreatDreamerTheme.archiveFont(14);
        smallFont = GreatDreamerTheme.archiveFont(12);
        buttonFont = GreatDreamerTheme.archiveFont(14);
        
        // Initialize buttons
        backButton = new Button(0, 0, BACK_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT,
                BACK_BUTTON_GRAPHIC_PREFIX + GameTexts.tr("common.back"));
        GreatDreamerTheme.styleSecondaryButton(backButton, buttonFont);
        
        // Set button action
        backButton.setOnClick(() -> {
            // Update character's last played time before returning
            selectedCharacter.setLastPlayedAt(java.time.LocalDateTime.now());
            SaveManager.getInstance().saveCharacter(selectedCharacter, characterSlot);
            
            // Return to character select
            Engine.instance().stateProcessor.setState(new CharacterSelectState());
        });

        String startButtonText = GameTexts.tr(isContinuingStory() ? "common.continue" : "common.start");
        startButton = new Button(0, 0, START_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, startButtonText + "  >");
        GreatDreamerTheme.styleDisabledButton(startButton, buttonFont);
        
        startButton.setOnClick(() -> {
            // State change will automatically trigger fade transition
            // Engine.instance().stateProcessor.setState(GameState.NORMAL);
        });
        
        // Create button array for keyboard navigation
        menuButtons = new Button[]{backButton, startButton};
        
        // Update character's last played time
        // selectedCharacter.setLastPlayedAt(java.time.LocalDateTime.now());

        skillTableTooltip = new Tooltip(SKILL_TABLE_TOOLTIP_DELAY_MS);
        GreatDreamerTheme.styleTooltip(skillTableTooltip, smallFont);

        rebuildSkillTable();
        statSummaryCells = new String[STAT_GRID_ROWS][STAT_GRID_COLS];
        statSummaryTooltips = new String[STAT_GRID_ROWS][STAT_GRID_COLS];
        rebuildStatSummaryPanel();
    }

    private void rebuildSkillTable() {
        CharacterSkill[] all = CharacterSkill.values().clone();
        Collator collator = Collator.getInstance(GameTexts.getActiveLocale());
        Arrays.sort(all, Comparator.comparing(CharacterSkill::getCode, collator));
        int n = all.length;
        int dataRows = (n + SKILLS_PER_TABLE_ROW - 1) / SKILLS_PER_TABLE_ROW;
        skillTableCells = new String[dataRows][SKILL_TABLE_COLS];
        skillTableTooltips = new String[dataRows][SKILL_TABLE_COLS];
        for (int r = 0; r < dataRows; r++) {
            for (int c = 0; c < SKILLS_PER_TABLE_ROW; c++) {
                // Column-major: locale alphabetical order runs top→bottom in column 1, then column 2, then 3.
                int i = c * dataRows + r;
                int col = c * 2;
                if (i < n) {
                    CharacterSkill s = all[i];
                    skillTableCells[r][col] = s.getCode();
                    skillTableCells[r][col + 1] = String.valueOf(selectedCharacter.getSkillValue(s));
                    String tip = skillTooltipText(s, skillTableCells[r][col + 1]);
                    skillTableTooltips[r][col] = tip;
                    skillTableTooltips[r][col + 1] = tip;
                } else {
                    skillTableCells[r][col] = "";
                    skillTableCells[r][col + 1] = "";
                    skillTableTooltips[r][col] = null;
                    skillTableTooltips[r][col + 1] = null;
                }
            }
        }
    }

    private static String skillTooltipText(CharacterSkill skill, String valueCell) {
        return skill.getDisplayName().toUpperCase(GameTexts.getActiveLocale())
                + "\n" + GameTexts.trf("gameplay.skill.rating_line", valueCell);
    }

    /** Pixels from dossier top to top of the 4×4 stat summary table (below the name/occupation divider). */
    private static final int STAT_TABLE_TOP_OFFSET = 82;
    private static final int STAT_TABLE_HEIGHT_PX = 100;
    private static final int SKILL_TABLE_GAP_BELOW_STATS = 10;
    private static final int DOSSIER_TABLE_BOTTOM_PAD = 14;

    private UiRect statSummaryTableBounds() {
        int top = dossierPanel.y + STAT_TABLE_TOP_OFFSET;
        return new UiRect(dossierPanel.x + 10, top, dossierPanel.width - 20, STAT_TABLE_HEIGHT_PX);
    }

    private UiRect skillTableBounds() {
        UiRect stat = statSummaryTableBounds();
        int tableTop = stat.bottom() + SKILL_TABLE_GAP_BELOW_STATS;
        int tableHeight = Math.max(64, dossierPanel.bottom() - DOSSIER_TABLE_BOTTOM_PAD - tableTop);
        return new UiRect(dossierPanel.x + 10, tableTop, dossierPanel.width - 20, tableHeight);
    }

    private void rebuildStatSummaryPanel() {
        if (statSummaryCells == null || statSummaryTooltips == null) {
            return;
        }
        Character c = selectedCharacter;
        statSummaryCells[0][0] = GameTexts.tr("stat.str.short") + " " + c.getStrength();
        statSummaryCells[0][1] = GameTexts.tr("stat.pow.short") + " " + c.getPower();
        statSummaryCells[0][2] = GameTexts.tr("stat.edu.short") + " " + c.getEducation();
        statSummaryCells[0][3] = GameTexts.tr("stat.con.short") + " " + c.getConstitution();
        statSummaryCells[1][0] = GameTexts.tr("stat.int.short") + " " + c.getIntelligence();
        statSummaryCells[1][1] = GameTexts.tr("stat.app.short") + " " + c.getAppearance();
        statSummaryCells[1][2] = GameTexts.tr("stat.lck.short") + " " + c.getLuck();
        statSummaryCells[1][3] = GameTexts.tr("stat.siz.short") + " " + c.getSize();
        statSummaryCells[2][0] = GameTexts.tr("stat.dex.short") + " " + c.getDexterity();
        statSummaryCells[2][1] = GameTexts.trf("gameplay.stat.hp_short", c.getCurrentHitPoints(), c.getMaxHitPoints());
        statSummaryCells[2][2] = GameTexts.trf("gameplay.stat.mp_short", c.getCurrentMagicPoints(), c.getMaxMagicPoints());
        statSummaryCells[2][3] = GameTexts.trf("gameplay.stat.san_short", c.getCurrentSanity(), c.getMaxSanityPoints());
        statSummaryCells[3][0] = GameTexts.trf("gameplay.stat.mov_short", c.getMoveRate());
        statSummaryCells[3][1] = GameTexts.trf("gameplay.stat.db_short", c.getDamageBonus());
        statSummaryCells[3][2] = GameTexts.trf("gameplay.stat.bld_short", c.getBuild());
        statSummaryCells[3][3] = GameTexts.trf("gameplay.stat.ddg_short", c.getDodgeValue());

        statSummaryTooltips[0][0] = statTooltipLine(GameTexts.tr("tooltip.stat.strength"), String.valueOf(c.getStrength()));
        statSummaryTooltips[0][1] = statTooltipLine(GameTexts.tr("tooltip.stat.power"), String.valueOf(c.getPower()));
        statSummaryTooltips[0][2] = statTooltipLine(GameTexts.tr("tooltip.stat.education"), String.valueOf(c.getEducation()));
        statSummaryTooltips[0][3] = statTooltipLine(GameTexts.tr("tooltip.stat.constitution"), String.valueOf(c.getConstitution()));
        statSummaryTooltips[1][0] = statTooltipLine(GameTexts.tr("tooltip.stat.intelligence"), String.valueOf(c.getIntelligence()));
        statSummaryTooltips[1][1] = statTooltipLine(GameTexts.tr("tooltip.stat.appearance"), String.valueOf(c.getAppearance()));
        statSummaryTooltips[1][2] = statTooltipLine(GameTexts.tr("tooltip.stat.luck"), String.valueOf(c.getLuck()));
        statSummaryTooltips[1][3] = statTooltipLine(GameTexts.tr("tooltip.stat.size"), String.valueOf(c.getSize()));
        statSummaryTooltips[2][0] = statTooltipLine(GameTexts.tr("tooltip.stat.dexterity"), String.valueOf(c.getDexterity()));
        statSummaryTooltips[2][1] = statTooltipLine(GameTexts.tr("tooltip.stat.hit_points"),
                GameTexts.trf("gameplay.tooltip.hp_detail", c.getCurrentHitPoints(), c.getMaxHitPoints()));
        statSummaryTooltips[2][2] = statTooltipLine(GameTexts.tr("tooltip.stat.magic_points"),
                GameTexts.trf("gameplay.tooltip.mp_detail", c.getCurrentMagicPoints(), c.getMaxMagicPoints()));
        statSummaryTooltips[2][3] = statTooltipLine(GameTexts.tr("tooltip.stat.sanity"),
                GameTexts.trf("gameplay.tooltip.san_detail", c.getCurrentSanity(), c.getMaxSanityPoints()));
        statSummaryTooltips[3][0] = statTooltipLine(GameTexts.tr("tooltip.stat.move_rate"), String.valueOf(c.getMoveRate()));
        statSummaryTooltips[3][1] = statTooltipLine(GameTexts.tr("tooltip.stat.damage_bonus"), c.getDamageBonus());
        statSummaryTooltips[3][2] = statTooltipLine(GameTexts.tr("tooltip.stat.build"), String.valueOf(c.getBuild()));
        statSummaryTooltips[3][3] = statTooltipLine(GameTexts.tr("tooltip.stat.dodge"),
                GameTexts.trf("gameplay.tooltip.dodge_detail", c.getDodgeValue()));
    }

    private static String statTooltipLine(String fullName, String detail) {
        return fullName.toUpperCase(GameTexts.getActiveLocale()) + "\n" + detail;
    }
    
    @Override
    public void onExit() {
        // Save character data when exiting
        if (selectedCharacter != null) {
            SaveManager.getInstance().saveCharacter(selectedCharacter, characterSlot);
        }
    }
    
    @Override
    public void tick() {
        // Handle keyboard navigation (visual order: back left, start right)
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_LEFT)) {
            selectedIndex = Math.max(0, selectedIndex - 1);
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_RIGHT)) {
            selectedIndex = Math.min(menuButtons.length - 1, selectedIndex + 1);
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            if (selectedIndex < menuButtons.length) {
                menuButtons[selectedIndex].click();
            }
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            backButton.click();
        }
    }
    
    @Override
    public void update() {
        updateLayout();

        // Update button states
        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        boolean mousePressed = Engine.instance().mouse.isLeftPressed();
        
        for (Button button : menuButtons) {
            button.update(mouseX, mouseY, mousePressed);
        }
        
        // Update selection state for keyboard navigation
        for (int i = 0; i < menuButtons.length; i++) {
            menuButtons[i].setSelected(i == selectedIndex);
        }

        rebuildStatSummaryPanel();
        updateDossierTableTooltips(mouseX, mouseY);
    }

    private void updateDossierTableTooltips(int mx, int my) {
        if (skillTableTooltip == null) {
            return;
        }
        String tip = null;
        if (dossierPanel != null && statSummaryTooltips != null && statSummaryTableBounds().contains(mx, my)) {
            tip = MenuTable.tooltipAt(statSummaryTableBounds(), STAT_GRID_COLS, null, statSummaryTooltips, mx, my);
        }
        if (tip == null && skillTableTooltips != null && dossierPanel != null && skillTableBounds().contains(mx, my)) {
            tip = MenuTable.tooltipAt(skillTableBounds(), SKILL_TABLE_COLS, null, skillTableTooltips, mx, my);
        }
        skillTableTooltip.update(tip != null, tip != null ? tip : "");
    }

    @Override
    public Cursor.SystemCursor getUiSystemCursor(int mx, int my) {
        if (menuButtons != null) {
            for (Button b : menuButtons) {
                if (b.contains(mx, my)) {
                    return Cursor.SystemCursor.Hand;
                }
            }
        }
        if (dossierPanel != null && statSummaryTableBounds().contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        if (dossierPanel != null && skillTableBounds().contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        return Cursor.SystemCursor.Arrow;
    }

    private void updateLayout() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        layout = GeneralMenuLayout.fromViewport(windowWidth, windowHeight);

        int bodyBottom = layout.content.bottom() - 78;
        int bodyHeight = Math.max(260, bodyBottom - layout.body.y);
        UiRect body = new UiRect(layout.body.x, layout.body.y, layout.body.width, bodyHeight);
        int leftWidth = Math.max(330, body.width * 38 / 100);
        dossierPanel = new UiRect(body.x, body.y, leftWidth, body.height);

        int storyX = dossierPanel.right() + GeneralMenuLayout.COLUMN_GAP;
        int storyWidth = Math.max(360, body.right() - storyX);
        int notesHeight = 90;
        int storyHeight = Math.max(190, body.height - notesHeight - 20);
        storyPanel = new UiRect(storyX, body.y, storyWidth, storyHeight);
        notesPanel = new UiRect(storyX, storyPanel.bottom() + 20, storyWidth, notesHeight);

        if (dossierChipPanel == null) {
            dossierChipPanel = new MenuChipPanel(dossierPanel, GameTexts.tr("panel.dossier.title"));
            storyChipPanel = new MenuChipPanel(storyPanel, GameTexts.tr("panel.story.title"));
            notesChipPanel = new MenuChipPanel(notesPanel, GameTexts.tr("panel.notes.title"));
        } else {
            dossierChipPanel.setBounds(dossierPanel);
            storyChipPanel.setBounds(storyPanel);
            notesChipPanel.setBounds(notesPanel);
            dossierChipPanel.setChipLabel(GameTexts.tr("panel.dossier.title"));
            storyChipPanel.setChipLabel(GameTexts.tr("panel.story.title"));
            notesChipPanel.setChipLabel(GameTexts.tr("panel.notes.title"));
        }

        MenuActionStrip.placeSecondaryBeforePrimary(layout, startButton, START_BUTTON_WIDTH, backButton,
                BACK_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, ACTION_BUTTON_GAP);
    }
    
    @Override
    public void render(Graphics g) {
        // Get window dimensions
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);

        GeneralMenuRenderer.drawPage(g2d, windowWidth, windowHeight);
        GeneralMenuRenderer.drawSubtleBackground(g2d, windowWidth, windowHeight);
        MenuScreenTitle.draw(g2d, layout.content, layout.content.right(), GameTexts.tr("screen.gameplay.title"),
                GameTexts.archiveStrapline("archive.strapline.incident_room"),
                new MenuScreenTitle.RightMetric(String.valueOf(selectedCharacter.getCompletedStoryCount()),
                        GameTexts.tr("screen.gameplay.metric.cases_closed"), 12, 96));

        drawStoryPlaceholder(g2d);
        drawNotesPanel(g2d);
        drawCharacterInfoPanel(g2d);

        backButton.render(g);
        startButton.render(g);
    }
    
    private void drawCharacterInfoPanel(Graphics2D g2d) {
        dossierChipPanel.draw(g2d);

        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.setFont(textFont);
        String characterInfo = GameTexts.trf("gameplay.character_line", selectedCharacter.getName(),
                selectedCharacter.getGender().getDisplayName(), selectedCharacter.getAge());
        g2d.drawString(characterInfo, dossierPanel.x + 18, dossierPanel.y + 28);

        g2d.setColor(GreatDreamerTheme.SELECTED);
        g2d.drawString(selectedCharacter.getLocalizedOccupation(), dossierPanel.x + 18, dossierPanel.y + 52);

        g2d.setColor(GreatDreamerTheme.LINE);
        g2d.drawLine(dossierPanel.x + 18, dossierPanel.y + 68, dossierPanel.right() - 18, dossierPanel.y + 68);

        MenuTable.draw(g2d, statSummaryTableBounds(), STAT_GRID_COLS, null, statSummaryCells);

        UiRect skillTableBounds = skillTableBounds();
        MenuTable.draw(g2d, skillTableBounds, SKILL_TABLE_COLS, null, skillTableCells);

        if (skillTableTooltip != null) {
            skillTableTooltip.render(g2d, Engine.instance().mouse.getX(), Engine.instance().mouse.getY(),
                    Engine.instance().getWidth(), Engine.instance().getHeight());
        }
    }
    
    private void drawStoryPlaceholder(Graphics2D g2d) {
        storyChipPanel.draw(g2d);

        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.setFont(textFont);
        String[] storyLines = {
            GameTexts.tr("story01.line01"),
            GameTexts.tr("story01.line02"),
            GameTexts.tr("story01.line03"),
            "",
            GameTexts.tr("story01.objective_heading"),
            GameTexts.tr("story01.objective01"),
            GameTexts.tr("story01.objective02"),
            "",
            GameTexts.tr("story01.future_heading"),
            GameTexts.tr("story01.future01"),
            GameTexts.tr("story01.future02")
        };
        drawLines(g2d, storyLines, storyPanel.x + 18, storyPanel.y + 28, 22);
    }

    private void drawNotesPanel(Graphics2D g2d) {
        notesChipPanel.draw(g2d);
        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.drawString(GameTexts.tr("notes.hint_return"), notesPanel.x + 18, notesPanel.y + 28);
        g2d.setColor(GreatDreamerTheme.MUTED_TEXT);
        g2d.drawString(GameTexts.tr("notes.placeholder"), notesPanel.x + 18, notesPanel.y + 52);
    }

    private void drawLines(Graphics2D g2d, String[] lines, int x, int startY, int lineHeight) {
        int lineY = startY;
        for (String line : lines) {
            if (!line.isEmpty()) {
                g2d.setColor(isLineHeading(line) ? GreatDreamerTheme.SELECTED : GreatDreamerTheme.TEXT);
                g2d.drawString(line, x, lineY);
            }
            lineY += lineHeight;
        }
    }

    private boolean isLineHeading(String line) {
        return line.endsWith(":") || line.equals(line.toUpperCase());
    }

    private boolean isContinuingStory() {
        return selectedCharacter.getTotalPlaytime() > 0
                || selectedCharacter.getCurrentStoryIndex() > 0
                || selectedCharacter.getCompletedStoryCount() > 0;
    }
}


