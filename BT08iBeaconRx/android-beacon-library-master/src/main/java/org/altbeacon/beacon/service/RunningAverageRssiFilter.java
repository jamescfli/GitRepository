package org.altbeacon.beacon.service;

import org.altbeacon.beacon.logging.LogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * Calculate a RSSI value on base of an arbitrary list of measured RSSI values
 * The list is clipped by a certain length at start and end and the average
 * is calculate by simple arithmetic average
 */
public class RunningAverageRssiFilter implements RssiFilter {

    private static final String TAG = "RunningAverageRssiFilter";
    public static long DEFAULT_SAMPLE_EXPIRATION_MILLISECONDS = 20000; /* 20 seconds */
    // Comment: samples exists for more than 20secs will be discarded
    private static long sampleExpirationMilliseconds = DEFAULT_SAMPLE_EXPIRATION_MILLISECONDS;
    private ArrayList<Measurement> mMeasurements = new ArrayList<Measurement>();

    @Override
    public void addMeasurement(Integer rssi) {
        Measurement measurement = new Measurement();
        measurement.rssi = rssi;
        measurement.timestamp = new Date().getTime();
        mMeasurements.add(measurement);
    }

    @Override
    public boolean noMeasurementsAvailable() {
        return mMeasurements.size() == 0;
    }

    @Override
    public double calculateRssi() {
        refreshMeasurements();  // Comment: drop outdated measurements
        int size = mMeasurements.size();
        // Comment: if size <= 2, then
        int startIndex = 0;
        int endIndex = size -1;
        // Comment: otherwise
        if (size > 2) {
            startIndex = size/10+1;
            endIndex = size-size/10-2;
        }

        double sum = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            sum += mMeasurements.get(i).rssi;
        }
        double runningAverage = sum/(endIndex-startIndex+1);

        LogManager.d(TAG, "Running average mRssi based on %s measurements: %s",
                size, runningAverage);
        return runningAverage;
    }

    private synchronized void refreshMeasurements() {
        Date now = new Date();
        ArrayList<Measurement> newMeasurements = new ArrayList<Measurement>();
        Iterator<Measurement> iterator = mMeasurements.iterator();
        while (iterator.hasNext()) {
            Measurement measurement = iterator.next();
            if (now.getTime() - measurement.timestamp < sampleExpirationMilliseconds ) {
                // Comment: drop outdated (older than 20secs) measurements
                newMeasurements.add(measurement);
            }
        }
        mMeasurements = newMeasurements;
        Collections.sort(mMeasurements);    // sort according to Comparable<Measurement>
    }

    private class Measurement implements Comparable<Measurement> {
        Integer rssi;
        long timestamp;

        // Commment: in order to drop top 10% and bottom 10% rssi values
        @Override
        public int compareTo(Measurement arg0) {
            return rssi.compareTo(arg0.rssi);
        }
    }

    public static void setSampleExpirationMilliseconds(long newSampleExpirationMilliseconds) {
        sampleExpirationMilliseconds = newSampleExpirationMilliseconds;
    }

}
