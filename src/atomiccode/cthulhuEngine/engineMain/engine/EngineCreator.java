package atomiccode.cthulhuEngine.engineMain.engine;

public class EngineCreator {

    protected static Engine init(EngineConfigs configs) {
        checkResFolderExists();
        if (configs == null) {
            throw new IllegalArgumentException("EngineConfigs cannot be null");
        }
        return new Engine(configs);
    }

    private static void checkResFolderExists() {
        if (!EngineFiles.RES_FOLDER.exists()) {
            throw new IllegalStateException("Resource folder not found: " + EngineFiles.RES_FOLDER.getAbsolutePath());
        }
    }
}
