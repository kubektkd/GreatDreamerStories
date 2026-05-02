package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GeneralMenuLayout;

/** Positions primary (right) and secondary (left of primary) action buttons like dossier menu screens. */
public final class MenuActionStrip {

    private MenuActionStrip() {
    }

    public static void placePrimaryRight(GeneralMenuLayout layout, Button primary, int primaryWidth, int height) {
        UiRect r = layout.rightAction(primaryWidth, height);
        primary.x = r.x;
        primary.y = r.y;
    }

    public static void placeSecondaryBeforePrimary(GeneralMenuLayout layout, Button primary, int primaryWidth,
                                                   Button secondary, int secondaryWidth, int height, int gap) {
        UiRect primaryRect = layout.rightAction(primaryWidth, height);
        primary.x = primaryRect.x;
        primary.y = primaryRect.y;
        UiRect secondaryRect = layout.before(primaryRect, secondaryWidth, gap);
        secondary.x = secondaryRect.x;
        secondary.y = secondaryRect.y;
    }
}
