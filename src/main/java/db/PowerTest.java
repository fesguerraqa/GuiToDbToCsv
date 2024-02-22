package db;

import tools.HelperTool;

public class PowerTest {

    private long testTime;
    private float minPower;
    private float maxPower;
    private float actualPower;
    private String testStatus;

    public PowerTest(long testTime, float minPower, float maxPower, float actualPower, String testStatus) {
        this.testTime = testTime;
        this.minPower = minPower;
        this.maxPower = maxPower;
        this.actualPower = actualPower;
        this.testStatus = testStatus;
    }

    public long getTestTime() {
        return this.testTime;
    }

    public float getMinPower() {
        return this.minPower;
    }

    public float getMaxPower() {
        return this.maxPower;
    }

    public float getActualPower(){
        return this.actualPower;
    }

    public String getTestStatus(){
        return this.testStatus;

    }

    /**
     * Helper printing tool for debugging.
     */
    private void printMe() {
        HelperTool.ezPrint("CSV File: "
                + "importTime=" + this.testTime
                + ", MIN=" + this.minPower
                + ", MAX=" + this.maxPower
                + ", ACTUAL=" + this.actualPower
                + ", TEST STATUS=" + this.testStatus
        );
    }

    public enum powerTestParams {
        test_time
        , min_power
        , max_power
        , actual_power
        , test_status
    }
}