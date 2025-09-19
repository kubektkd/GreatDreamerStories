package atomiccode.utils.math;

import java.util.LinkedList;

public class RollingAverage {

    private final int max;
    private final LinkedList<Float> values = new LinkedList<>();
    private float sum = 0;

    public RollingAverage(int max) {
        this.max = max;
    }

    public void addValue(float value) {
        if (values.size() >= max) {
            sum -= values.removeFirst();
        }
        values.addLast(value);
        sum += value;
    }

    public float getAverage() {
        return values.isEmpty() ? 0f : sum / values.size();
    }
}
