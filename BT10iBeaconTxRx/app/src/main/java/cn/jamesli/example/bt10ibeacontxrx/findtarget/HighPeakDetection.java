package cn.jamesli.example.bt10ibeacontxrx.findtarget;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jamesli on 15/10/15.
 */
public class HighPeakDetection {

        private List<Float> values = new LinkedList<>();
        private int lengthForSelectPeak;
        private int peakCounter = 0;
        private float thresholdForPeakDetection = 3;
        private int peakIndex;


        /**
         *
         * @param length the maximum lengthForSelectPeak
         */
        public HighPeakDetection(int length, float threshold) {
            if (length <= 0) {
                throw new IllegalArgumentException("lengthForSelectPeak must be greater than zero");
            }
            this.lengthForSelectPeak = length;
            this.peakIndex = this.lengthForSelectPeak / 2;
            this.thresholdForPeakDetection = threshold;
        }

        public int getPeaksCounter() {
            return peakCounter;
        }

        //!!!!!!!!!!!!!!!!Attetion: only available when  findPeak return true!!!!!!!!!!!!!!!!!!!
        public  float getcurrentpeakvalue() {
            return 	((float) values.get(peakIndex));
        }


        //add new vlaue to ring buffer
        //check peak:peakIndex is lengthForSelectPeak/2, 3 conditions that peakIndex is real peak:
        //1. 0-peakIndex is increasing
        //2. peakIndex-values.size() is decreasing
        //3. values.get(peakIndex)>thresholdForPeakDetection

        public synchronized boolean findPeak(float value) {
            float behind,former;
            if (values.size() == lengthForSelectPeak && lengthForSelectPeak > 0) {
                values.remove(0);
            }
            values.add(new Float(value));

            if(values.size() == lengthForSelectPeak) {
                for(int i=0;i< peakIndex;i++) {
                    behind = ((float) values.get(i+1));
                    former = ((float) values.get(i));
                    if(behind<=former)
                        return false;
                }
                //IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
                for(int i= peakIndex +1;i<values.size();i++) {
                    behind = ((float) values.get(i));
                    former = ((float) values.get(i-1));
                    if(behind>=former)
                        return false;
                }
                if(((float) values.get(peakIndex)) >= thresholdForPeakDetection) {
                    peakCounter++;
                    return true;
                }
            }
            return false;
        }
}
