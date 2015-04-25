// IRemoteCalc.aidl
package cn.nec.nlc.jamesli.tools.activitytest67aidl;

// Declare any non-default types here with import statements

interface IRemoteCalc {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
