package cn.jamesli.example.bt10ibeacontxrx.findtarget;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jamesli on 15/10/15.
 */
public class SimpleMovingAverage {
    private List<Float> valueList = new LinkedList<>();
    private int lengthOfSmoothedValue;
    private float accumulatedSum;
    private float currentAverage;

    public SimpleMovingAverage(int length) {
        if (length <= 0)
            throw new IllegalArgumentException("Length of SMA should be greater than 0");
        this.lengthOfSmoothedValue = length;
        accumulatedSum = 0;
        currentAverage = 0;
    }

    public float getCurrentAverage() {
        return currentAverage;
    }

    /**
     * Compute the moving average.
     * Synchronised so that no changes in the underlying data is made during calculation.
     * @param newValue The value
     * @return currentAverage after computation
     */
    public synchronized float add(float newValue) {
        if (valueList.size() == lengthOfSmoothedValue) {
            accumulatedSum -= (float) valueList.get(0);
            valueList.remove(0);
        }
        accumulatedSum += newValue;
        valueList.add(new Float(newValue));
        currentAverage = accumulatedSum / valueList.size();
        return currentAverage;
    }
}
