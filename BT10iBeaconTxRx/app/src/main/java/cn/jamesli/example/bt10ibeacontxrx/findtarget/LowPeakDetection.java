package cn.jamesli.example.bt10ibeacontxrx.findtarget;

import java.util.LinkedList;

/**
 * Created by jamesli on 15/10/17. TODO could be further refactorized with HighPeakDetection
 */
public class LowPeakDetection {
    private LinkedList values = new LinkedList();
    private int length;
    private int peak_index;

    public LowPeakDetection(int length)
    {
        if (length <= 0)
        {
            throw new IllegalArgumentException("length must be greater than zero");
        }
        this.length = length;
        this.peak_index = this.length/2;
    }

    // Attention: only available when  findPeak return true !!
    public  float getCurrentPeakValue()
    {
        return (float) values.get(peak_index);
    }


    //add new vlaue to ring buffer
    //check peak:peak_index is length/2, 3 conditions that peak_index is real peak:
    //1. 0-peak_index is increasing
    //2. peak_index-values.size() is decreasing
    //3. values.get(peak_index)>thresh

    public synchronized boolean findPeak(float value)
    {
        float first,second;

        if (values.size() == length && length > 0)
            values.removeFirst();
        values.addLast(value);

        if(values.size() == length)
        {
            for(int i=0;i<peak_index;i++)
            {
                first = (float) values.get(i);
                second = (float) values.get(i + 1);

                if(first<second)//low peak
                    return false;
            }
            //IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
            for(int i=peak_index+1;i<values.size();i++)
            {
                first = (float) values.get(i - 1);
                second = (float) values.get(i);

                if(first>second)//low peak
                    return false;
            }
            return true;
        }
        return false;
    }
}
