package cn.jamesli.example.bt12staggeredgrid;

import java.util.ArrayList;

/**
 * Created by jamesli on 15/9/3.
 */
public class SampleData {
    public static final int SAMPLE_DATA_ITEM_COUNT = 30;

    public static ArrayList<String> generateSampleData() {
        final ArrayList<String> data = new ArrayList<String>(SAMPLE_DATA_ITEM_COUNT);

        for (int i = 0; i < SAMPLE_DATA_ITEM_COUNT; i++) {
            data.add("SAMPLE #");
        }

        return data;
    }
}
