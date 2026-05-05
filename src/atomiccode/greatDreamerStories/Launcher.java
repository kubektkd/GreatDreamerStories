package atomiccode.greatDreamerStories;

import atomiccode.greatDreamerStories.configs.EngineConfigs_Build;

public class Launcher {

    public static void main(String[] args) {
        Game.launch(new EngineConfigs_Build(GameVersion.CURRENT));
    }
}