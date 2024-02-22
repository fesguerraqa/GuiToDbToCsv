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
            printStatus("Prior to RUN TEST");
            runTest();
            printStatus("AFTER run TEST");
        }
    }

    private void printStatus(String s) {
        HelperTool.ezPrint(s + "--> Test Status: " + getTestStatus() + ", Verbose: " + getTestStatusVerbose());
    }

    private boolean runTest(){
        boolean inRange = false;

        if (this.actualPower < this.maxPower && this.actualPower > this.minPower){
            inRange = true;
            testStatus = testStatusEnum.PASS.toString();
        }

        this.testStatusVerbose = this.testStatus;

        return inRange;
    }

    public String getTestStatusVerbose(){
        return this.testStatusVerbose;
    }

    private boolean checkForValidityOfInputs(){

        boolean inputValuesValid = true;

        this.testStatus = testStatusEnum.FAIL.toString();

        try {

            if (this.minPower > this.maxPower) {

                this.testStatusVerbose = "INVALID: MIN CANNOT be greater than MAX";
                this.testStatus = testStatusEnum.INVALID.toString();
                inputValuesValid = false;
//                showErrorMessageBox(errMinMax);
            }
            else if (this.minPower == this.maxPower) {

                this.testStatusVerbose = "INVALID: MIN and MAX Cannot be EQUAL";
                this.testStatus = testStatusEnum.INVALID.toString();
                inputValuesValid = false;
//                showErrorMessageBox(errEqual);
            }
        }
        catch (Exception ex){

            String exception = ex.getMessage();
            this.testStatusVerbose = testStatusEnum.INVALID.toString() + " INPUT FOUND: " + exception;
            this.testStatus = testStatusEnum.INVALID.toString();
            //showErrorMessageBox("Invalid INPUT FOUND: " + exception);
        }

        return inputValuesValid;
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

    public enum testStatusEnum{
        PASS
        , FAIL
        , INVALID
    }
}