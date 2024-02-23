package db;

import tools.HelperTool;

public class PowerTest {

    private long testTime;
    private float minPower;
    private float maxPower;
    private float actualPower;
    private String testStatus;
    private String testStatusVerbose;

    public PowerTest(long testTime, float minPower, float maxPower, float actualPower) {
        this.testTime = testTime;
        this.minPower = minPower;
        this.maxPower = maxPower;
        this.actualPower = actualPower;

        checkForValidityOfInputs();

        if(!this.testStatus.equals(testStatusEnum.INVALID.toString())){
            runTest();
        }
    }

    public String getTestStatusVerbose(){
        return this.testStatusVerbose;
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
     * Run test here and populate test result.
     * @return Returns pass is Actual Power is between Min and Max
     */
    private boolean runTest(){
        boolean inRange = true;
        testStatus = testStatusEnum.PASS.toString();

        if (this.actualPower > this.maxPower || this.actualPower < this.minPower){
            inRange = false;
            testStatus = testStatusEnum.FAIL.toString();
        }

        this.testStatusVerbose = this.testStatus;

        return inRange;
    }

    /**
     * Checks for invalid characters or invalid value combination of Min and Max.
     * @return
     */
    private boolean checkForValidityOfInputs(){

        boolean inputValuesValid = true;

        this.testStatus = testStatusEnum.FAIL.toString();

        try {

            if (this.minPower > this.maxPower) {

                this.testStatusVerbose = "INVALID: MIN CANNOT be greater than MAX";
                this.testStatus = testStatusEnum.INVALID.toString();
                inputValuesValid = false;
            }
            else if (this.minPower == this.maxPower) { //TODO: There is an argument that this can be a valid option.

                this.testStatusVerbose = "INVALID: MIN and MAX Cannot be EQUAL";
                this.testStatus = testStatusEnum.INVALID.toString();
                inputValuesValid = false;
            }
        }
        catch (Exception ex){

            String exception = ex.getMessage();
            this.testStatusVerbose = "INVALID INPUT FOUND: " + exception;
            this.testStatus = testStatusEnum.INVALID.toString();
        }

        return inputValuesValid;
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

    public enum testStatusEnum{
        PASS
        , FAIL
        , INVALID
    }
}