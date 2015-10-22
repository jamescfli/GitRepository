package cn.jamesli.example.bt10ibeacontxrx.findtarget;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jamesli on 15/10/17.
 */
public class LowPeakDetection {

    private List<Float> valueList = new LinkedList<>();
    private int lengthOfBufferedList;
    private int indexOfPeak;

    public LowPeakDetection(int length) {
        if (length <= 2) {
            throw new IllegalArgumentException("lengthOfBufferedList must be >= 3");
        }
        this.lengthOfBufferedList = length;
        this.indexOfPeak = this.lengthOfBufferedList / 2;
    }

    // usage note: only available when findPeak return true
    public  float getCurrentPeakValue()
    {
        return (float) valueList.get(indexOfPeak);
    }

    // add new value to ring buffer
    // check peak:indexOfPeak is lengthOfBufferedList/2, 3 conditions that indexOfPeak is real peak:
    // 1. 0-indexOfPeak is increasing
    // 2. indexOfPeak-valueList.size() is decreasing

    public synchronized boolean findPeak(float value) {
        float former;
        float behind;

        if (valueList.size() == lengthOfBufferedList) {
            valueList.remove(0);    // list full, delete the first item
        }
        valueList.add(value);

        if (valueList.size() == lengthOfBufferedList) {  // need to form a full list
            for(int i = 0; i < indexOfPeak; i++) {
                former = (float) valueList.get(i);
                behind = (float) valueList.get(i + 1);

                if (former < behind)//low peak
                    return false;
            }
            //IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
            for(int i = indexOfPeak+1; i< valueList.size(); i++) {
                former = (float) valueList.get(i - 1);
                behind = (float) valueList.get(i);

                if (former > behind)//low peak
                    return false;
            }
            return true;
        }
        return false;   // no enough data yet
    }
}
