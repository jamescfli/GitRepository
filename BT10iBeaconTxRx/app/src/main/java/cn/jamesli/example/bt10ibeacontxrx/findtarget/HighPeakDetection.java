package cn.jamesli.example.bt10ibeacontxrx.findtarget;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jamesli on 15/10/15.
 */
public class HighPeakDetection {

        private List<Float> valueList = new LinkedList<>();
        private int lengthOfBufferedList;
//        private int peakCounter = 0;
        private float thresholdForPeakDetection = 3;
        private int peakIndex;

        /**
         * @param length the maximum lengthOfBufferedList
         */
        public HighPeakDetection(int length, float threshold) {
            if (length <= 2) {
                throw new IllegalArgumentException("lengthOfBufferedList must be >= 3 to form a peak");
            }
            this.lengthOfBufferedList = length;
            this.peakIndex = this.lengthOfBufferedList / 2;
            this.thresholdForPeakDetection = threshold;
        }

//        public int getPeaksCounter() {
//            return peakCounter;
//        }

//        // usage note: only available when findPeak return true
//        public  float getCurrentPeakValue() {
//            return 	((float) valueList.get(peakIndex));
//        }


        // add new value to ring buffer
        // check peak:peakIndex is lengthOfBufferedList/2, 3 conditions that peakIndex is real peak:
        // 1. 0-peakIndex is increasing
        // 2. peakIndex-valueList.size() is decreasing
        // 3. valueList.get(peakIndex)>thresholdForPeakDetection

        public synchronized boolean findPeak(float value) {
            float former;
            float behind;
            if (valueList.size() == lengthOfBufferedList) {
                valueList.remove(0);
            }
            valueList.add(value);

            if (valueList.size() == lengthOfBufferedList) {
                for(int i=0; i < peakIndex; i++) {
                    behind = ((float) valueList.get(i+1));
                    former = ((float) valueList.get(i));
                    if(behind <= former)
                        return false;
                }
                //IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
                for(int i = peakIndex+1; i< valueList.size(); i++) {
                    behind = (float) valueList.get(i);
                    former = (float) valueList.get(i-1);
                    if(behind >= former)
                        return false;
                }
                // one extra condition by threshold
                if(((float) valueList.get(peakIndex)) >= thresholdForPeakDetection) {
//                    peakCounter++;
                    return true;
                }
            }
            return false;   // no enough data yet, or high peak < threshold
        }
}
