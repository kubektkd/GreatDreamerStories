package atomiccode.utils.math;

import java.util.LinkedList;

public class RollingAverage {

    private final int max;
    private final LinkedList<Float> values = new LinkedList<Float>();

    public RollingAverage(int max) {
        this.max = max;
    }

    public void addValue(float value) {
        if (values.size() >= max) {
            values.removeFirst();
        }
        values.addLast(value);
    }

    public float getAverage() {
        float total = 0;
        for (Float f : values) {
            total += f;
        }
        return total / values.size();
    }
}
