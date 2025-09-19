package atomiccode.cthulhuEngine.inputsOutputs.timing;

import atomiccode.utils.math.RollingAverage;

public class FrameTimer {

    private static final float MAX_DELTA = 0.1f;
    private static final float STABLE_TIME = 1.5f;
    private static final int ROLL_AVG_COUNT = 20;

    private static final int NANOS_IN_SECOND = 1000 * 1000 * 1000;

    private final float idealDelta;

    private final RollingAverage deltaAverage = new RollingAverage(ROLL_AVG_COUNT);

    private long lastFrameTime;
    private float delta;

    private float timeSinceStart = 0;

    public FrameTimer(float idealFps){
        this.idealDelta = 1f / idealFps;
        this.delta = idealDelta;
        this.lastFrameTime = getCurrentTime();
    }

    public void update(){
        long currentFrameTime = getCurrentTime();
        float frameLength = Math.min(MAX_DELTA, (currentFrameTime - lastFrameTime) / (float) NANOS_IN_SECOND);
        this.lastFrameTime = currentFrameTime;

        // warm-up stabilization
        if (timeSinceStart < STABLE_TIME) {
            frameLength = idealDelta;
        }

        deltaAverage.addValue(frameLength);
        this.delta = deltaAverage.getAverage();
        timeSinceStart += frameLength;
    }

    public float getDelta(){
        return delta;
    }

    /**
     * @return Time in seconds since the engine started.
     */
    public float getTime() {
        return timeSinceStart;
    }

    private long getCurrentTime() {
        return System.nanoTime();
    }
}
