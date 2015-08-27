package cn.jamesli.example.bt08ibeaconrx;

import org.altbeacon.beacon.distance.CurveFittedDistanceCalculator;
import org.altbeacon.beacon.distance.DistanceCalculator;

/**
 * Created by jamesli on 15/8/25.
 */
public class ModelCustomizedDistanceCalculator implements DistanceCalculator {
    private double coefficient1,coefficient2,coefficient3;
    private DistanceCalculator mDistanceCalculator;

    public ModelCustomizedDistanceCalculator(double coefficient1, double coefficient2, double coefficient3) {
        this.coefficient1 = coefficient1;
        this.coefficient2 = coefficient2;
        this.coefficient3 = coefficient3;

        this.mDistanceCalculator = new CurveFittedDistanceCalculator(coefficient1,coefficient2,coefficient3);;

    }

    @Override
    public double calculateDistance(int i, double v) {

        return mDistanceCalculator.calculateDistance(i,v);
    }
}
