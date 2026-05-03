package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.character.SaveManager;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GeneralMenuLayout;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;
import atomiccode.greatDreamerStories.ui.menu.MenuActionStrip;
import atomiccode.greatDreamerStories.ui.menu.MenuChipPanel;
import atomiccode.greatDreamerStories.ui.menu.MenuScreenTitle;

import com.badlogic.gdx.graphics.Cursor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

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
    }

    @Override
    public Cursor.SystemCursor getUiSystemCursor(int mx, int my) {
        if (backButton.contains(mx, my)) {
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

        dossierChipPanel = new MenuChipPanel(dossierPanel, "INVESTIGATOR DOSSIER");
        storyChipPanel = new MenuChipPanel(storyPanel, "CASE BOARD");
        notesChipPanel = new MenuChipPanel(notesPanel, "FIELD NOTES");

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
        List<String> skillLines = selectedCharacter.getAllSkillsSummaryLines(8);
        String[] dossierLines = new String[skillLines.size() + 6];
        dossierLines[0] = String.format("STR:%d  POW:%d  EDU:%d CON:%d", selectedCharacter.getStrength(), selectedCharacter.getPower(), selectedCharacter.getEducation(), selectedCharacter.getConstitution());
        dossierLines[1] = String.format("INT:%d  APP:%d  LCK:%d  SIZ:%d  DEX:%d", selectedCharacter.getIntelligence(), selectedCharacter.getAppearance(), selectedCharacter.getLuck(), selectedCharacter.getSize(), selectedCharacter.getDexterity());
        dossierLines[2] = "";
        dossierLines[3] = "SKILLS:";
        for (int i = 0; i < skillLines.size(); i++) {
            dossierLines[i + 4] = skillLines.get(i);
        }
        dossierLines[dossierLines.length - 2] = "";
        dossierLines[dossierLines.length - 1] = "Stories completed: " + getCompletedStoryCount() + "/" + Character.MAX_STORIES;

        drawLines(g2d, dossierLines, dossierPanel.x + 18, dossierPanel.y + 102, 20);
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


