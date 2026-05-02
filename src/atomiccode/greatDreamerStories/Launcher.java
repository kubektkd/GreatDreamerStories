package atomiccode.greatDreamerStories;

import atomiccode.greatDreamerStories.configs.EngineConfigs_Build;

public class Launcher {

    private static final String VERSION = "0.0.1";

    public static void main(String[] args) {
        Game.launch(new EngineConfigs_Build(VERSION));
    }
}