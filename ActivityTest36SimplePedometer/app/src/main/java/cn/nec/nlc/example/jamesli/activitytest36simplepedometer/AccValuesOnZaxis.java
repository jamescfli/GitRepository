package cn.nec.nlc.example.jamesli.activitytest36simplepedometer;


import java.util.LinkedList;

public class AccValuesOnZaxis {
    private LinkedList<Float> rawAccOnZaxis;
    private int lengthOfSmaList;    // 10 as empirical default
    private int lengthOfPeakList;   //  7 as empirical default
    private int indexOfPeak;
    private float thresholdPeak;

    private LinkedList<Float> smoothedAccOnZaxis;

    public AccValuesOnZaxis(int aLengthOfSmaList, int aLengthOfPeakList, float aThresholdPeak) {
        this.lengthOfSmaList = aLengthOfSmaList;
        this.lengthOfPeakList = aLengthOfPeakList;
        if (lengthOfPeakList > lengthOfSmaList) {
            throw new IllegalArgumentException("Peak detect list is longer than SMA list");
        }
        this.thresholdPeak = aThresholdPeak;
        indexOfPeak = lengthOfPeakList/2;

        rawAccOnZaxis = new LinkedList<>();
        smoothedAccOnZaxis = new LinkedList<>();
    }

    // clear both raw and sma list
    public void clearSavedAccData() {
        rawAccOnZaxis.clear();
        smoothedAccOnZaxis.clear();
    }

    // add new measurement from accelerometer and update sma list accordingly
    public void addNewDataToRawList(float aNewMeasurement) {
        rawAccOnZaxis.add(aNewMeasurement);
        if (rawAccOnZaxis.size() > lengthOfSmaList) {
            // exceed the length limit of raw data
            rawAccOnZaxis.remove(0);    // delete the oldest one
        }
        // renew smoothedAccOnZaxis list according to new value
        updateSmoothedAccOnZaxis();
    }

    private void updateSmoothedAccOnZaxis() {
        float newAverage;
        // update smoothedAccOnZaxis list until accumulated enough raw data in rawAccOnZaxis list
        if (rawAccOnZaxis.size() == lengthOfSmaList) {
            if (smoothedAccOnZaxis.size() < lengthOfSmaList) {
                newAverage = 0; // exhausted average
                for (int i = 0; i < lengthOfSmaList; i++) {
                    newAverage = newAverage + rawAccOnZaxis.get(i);
                }
                newAverage = newAverage/lengthOfSmaList;
                smoothedAccOnZaxis.add(newAverage);
            } else if (smoothedAccOnZaxis.size() == lengthOfSmaList) {
                newAverage = 0;
                for (int i = 0; i < lengthOfSmaList; i++) {
                    newAverage = newAverage + rawAccOnZaxis.get(i);
                }
                newAverage = newAverage/lengthOfSmaList;
                smoothedAccOnZaxis.add(newAverage);
                smoothedAccOnZaxis.remove(0);   // keep the length of the list the same
            }
        }
    }

    // find the peak point from acc series on Z-axis
    public synchronized boolean findPeak() {
        float later;
        float former;

        // accumulate enough data to count peaks
        if (smoothedAccOnZaxis.size() == lengthOfSmaList) {
            for (int i = (lengthOfSmaList-lengthOfPeakList); i < (lengthOfSmaList-lengthOfPeakList)
                    +indexOfPeak; i++) {
                later = smoothedAccOnZaxis.get(i+1).floatValue();
                former = smoothedAccOnZaxis.get(i).floatValue();
                if (later <= former) {
                    return false;   // not the peak for the current series
                }
            }
            for (int i = (lengthOfSmaList-lengthOfPeakList)+indexOfPeak+1; i < lengthOfSmaList; i++) {
                later = smoothedAccOnZaxis.get(i).floatValue();
                former = smoothedAccOnZaxis.get(i-1).floatValue();
                if (later >= former) {
                    return false;
                }
            }
            if (smoothedAccOnZaxis.get((lengthOfSmaList - lengthOfPeakList) + indexOfPeak)
                .floatValue() >= thresholdPeak) {
                return true;
            }
        }
        // for all other cases
        return false;
    }
}
