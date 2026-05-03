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

import com.badlogic.gdx.graphics.Cursor;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main gameplay state where the story unfolds.
 * Currently serves as a placeholder for the actual visual novel/RPG gameplay.
 */
public class GameplayState implements State {
    
    private final Character selectedCharacter;
    private final int characterSlot;
    
    private Button backButton;
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

    private static final int SKILL_TABLE_COLS = 6;
    private static final int SKILLS_PER_TABLE_ROW = 3;
    private static final long SKILL_TABLE_TOOLTIP_DELAY_MS = 300;

    private String[][] skillTableCells;
    private String[][] skillTableTooltips;
    private int skillTableRows;
    private Tooltip skillTableTooltip;
    
    public GameplayState(Character character, int characterSlot) {
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
        backButton = new Button(0, 0, 210, 48, "<  CASE FILES");
        GreatDreamerTheme.styleArchiveButton(backButton, buttonFont);
        
        // Set button action
        backButton.setOnClick(() -> {
            // Update character's last played time before returning
            selectedCharacter.setLastPlayedAt(java.time.LocalDateTime.now());
            SaveManager.getInstance().saveCharacter(selectedCharacter, characterSlot);
            
            // Return to character select
            Engine.instance().stateProcessor.setState(new CharacterSelectState());
        });
        
        // Create button array for keyboard navigation
        menuButtons = new Button[]{backButton};
        
        // Update character's last played time
        selectedCharacter.setLastPlayedAt(java.time.LocalDateTime.now());

        skillTableTooltip = new Tooltip(SKILL_TABLE_TOOLTIP_DELAY_MS);
        GreatDreamerTheme.styleTooltip(skillTableTooltip, smallFont);

        rebuildSkillTable();
    }

    private void rebuildSkillTable() {
        CharacterSkill[] all = CharacterSkill.values();
        int n = all.length;
        int dataRows = (n + SKILLS_PER_TABLE_ROW - 1) / SKILLS_PER_TABLE_ROW;
        skillTableRows = 1 + dataRows;
        skillTableCells = new String[skillTableRows][SKILL_TABLE_COLS];
        skillTableTooltips = new String[skillTableRows][SKILL_TABLE_COLS];
        String[] header = skillTableCells[0];
        header[0] = "SKILL";
        header[1] = "%";
        header[2] = "SKILL";
        header[3] = "%";
        header[4] = "SKILL";
        header[5] = "%";
        for (int r = 0; r < dataRows; r++) {
            int outRow = r + 1;
            for (int c = 0; c < SKILLS_PER_TABLE_ROW; c++) {
                int i = r * SKILLS_PER_TABLE_ROW + c;
                int col = c * 2;
                if (i < n) {
                    CharacterSkill s = all[i];
                    skillTableCells[outRow][col] = s.getCode();
                    skillTableCells[outRow][col + 1] = String.valueOf(selectedCharacter.getSkillValue(s));
                    String tip = skillTooltipText(s, skillTableCells[outRow][col + 1]);
                    skillTableTooltips[outRow][col] = tip;
                    skillTableTooltips[outRow][col + 1] = tip;
                } else {
                    skillTableCells[outRow][col] = "";
                    skillTableCells[outRow][col + 1] = "";
                    skillTableTooltips[outRow][col] = null;
                    skillTableTooltips[outRow][col + 1] = null;
                }
            }
        }
    }

    private static String skillTooltipText(CharacterSkill skill, String valueCell) {
        return skill.getDisplayName().toUpperCase() + "\nRating: " + valueCell + "%";
    }

    private UiRect skillTableBounds() {
        int tableTop = dossierPanel.y + 136;
        int footerBand = 28;
        int tableHeight = Math.max(48, dossierPanel.bottom() - 18 - footerBand - tableTop);
        return new UiRect(dossierPanel.x + 10, tableTop, dossierPanel.width - 20, tableHeight);
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
        // Handle keyboard navigation
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

        updateSkillTableTooltip(mouseX, mouseY);
    }

    private void updateSkillTableTooltip(int mx, int my) {
        if (skillTableTooltip == null || skillTableTooltips == null) {
            return;
        }
        UiRect tb = skillTableBounds();
        String tip = MenuTable.tooltipAt(tb, skillTableRows, SKILL_TABLE_COLS, skillTableTooltips, mx, my);
        skillTableTooltip.update(tip != null, tip != null ? tip : "");
    }

    @Override
    public Cursor.SystemCursor getUiSystemCursor(int mx, int my) {
        if (backButton.contains(mx, my)) {
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
            dossierChipPanel = new MenuChipPanel(dossierPanel, "INVESTIGATOR DOSSIER");
            storyChipPanel = new MenuChipPanel(storyPanel, "CASE BOARD");
            notesChipPanel = new MenuChipPanel(notesPanel, "FIELD NOTES");
        } else {
            dossierChipPanel.setBounds(dossierPanel);
            storyChipPanel.setBounds(storyPanel);
            notesChipPanel.setBounds(notesPanel);
        }

        MenuActionStrip.placePrimaryRight(layout, backButton, 210, 48);
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
        MenuScreenTitle.draw(g2d, layout.content, layout.content.right(), "ACTIVE INVESTIGATION",
                "STOKSJÖ POLICE ARCHIVE // INCIDENT ROOM",
                new MenuScreenTitle.RightMetric(String.valueOf(getCompletedStoryCount()), "CASES CLOSED", 12, 96));

        drawCharacterInfoPanel(g2d);
        drawStoryPlaceholder(g2d);
        drawNotesPanel(g2d);

        backButton.render(g);
    }
    
    private void drawCharacterInfoPanel(Graphics2D g2d) {
        dossierChipPanel.draw(g2d);

        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.setFont(textFont);
        String characterInfo = selectedCharacter.getName() + " (" + selectedCharacter.getGender().getDisplayName() + ")";
        g2d.drawString(characterInfo, dossierPanel.x + 18, dossierPanel.y + 28);

        g2d.setColor(GreatDreamerTheme.SELECTED);
        g2d.drawString(selectedCharacter.getOccupation(), dossierPanel.x + 18, dossierPanel.y + 52);

        g2d.setColor(GreatDreamerTheme.LINE);
        g2d.drawLine(dossierPanel.x + 18, dossierPanel.y + 74, dossierPanel.right() - 18, dossierPanel.y + 74);

        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.setFont(smallFont);
        String[] statLines = {
                String.format("STR:%d  POW:%d  EDU:%d  CON:%d", selectedCharacter.getStrength(), selectedCharacter.getPower(),
                        selectedCharacter.getEducation(), selectedCharacter.getConstitution()),
                String.format("INT:%d  APP:%d  LCK:%d  SIZ:%d  DEX:%d", selectedCharacter.getIntelligence(),
                        selectedCharacter.getAppearance(), selectedCharacter.getLuck(), selectedCharacter.getSize(),
                        selectedCharacter.getDexterity())
        };
        // Attribute summary (not part of MenuTable; the skill grid is drawn below).
        drawLines(g2d, statLines, dossierPanel.x + 18, dossierPanel.y + 96, 18);

        UiRect skillTableBounds = skillTableBounds();
        MenuTable.draw(g2d, skillTableBounds, skillTableRows, SKILL_TABLE_COLS, skillTableCells, smallFont, 1);

        g2d.setColor(GreatDreamerTheme.MUTED_TEXT);
        g2d.setFont(smallFont);
        String footer = "Stories completed: " + getCompletedStoryCount() + "/" + Character.MAX_STORIES;
        FontMetrics fm = g2d.getFontMetrics(smallFont);
        g2d.drawString(footer, dossierPanel.x + 18, dossierPanel.bottom() - 14 - Math.max(0, fm.getDescent() - 2));

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
            "The fog rolls in from the fjord as the station phone keeps ringing.",
            "A strange case waits in Stoksjö, buried under snow, old records,",
            "and the stories locals only tell after dark.",
            "",
            "Current objective:",
            "Review the first reports, question witnesses, and start building",
            "a timeline before the town closes ranks around the truth.",
            "",
            "Future implementation:",
            "Dialogue choices, clues, map exploration, inventory, and",
            "turn-based encounters will unfold from this state."
        };
        drawLines(g2d, storyLines, storyPanel.x + 18, storyPanel.y + 28, 22);
    }

    private void drawNotesPanel(Graphics2D g2d) {
        notesChipPanel.draw(g2d);
        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.drawString("Press ESC or use the case files button to return to investigator selection.", notesPanel.x + 18, notesPanel.y + 28);
        g2d.setColor(GreatDreamerTheme.MUTED_TEXT);
        g2d.drawString("This panel will later hold active clues, leads, and inventory reminders.", notesPanel.x + 18, notesPanel.y + 52);
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

    private int getCompletedStoryCount() {
        int completedStories = 0;
        for (boolean completed : selectedCharacter.getCompletedStories()) {
            if (completed) {
                completedStories++;
            }
        }
        return completedStories;
    }
}


