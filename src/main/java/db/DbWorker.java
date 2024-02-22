package db;

import tools.HelperTool;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class DbWorker {

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/gui_to_db_to_csv";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PWD = "mysqlt3st!";
    private static final String DIR_CSV =
            "/Users/jjesguerramba2023/IdeaProjects/TestAuto/GuiToDbToCsv/artifacts/csv/";

    public Connection dbConnection;
    public PreparedStatement statement;

    private void connectDb() throws SQLException {
        dbConnection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PWD);
    }

    private void disconnectDb() throws SQLException {
        dbConnection.close();
    }

    /**
     * DB method to create a Power Test Table. This is only used during setup.
     * @throws SQLException
     */
    public void createPowerReadTable() throws SQLException {

        connectDb();

        String createTable = "CREATE TABLE " + dbTable.power_test
                + "(" + PowerTest.powerTestParams.test_time + " BIGINT NOT NULL"
                + ", " + PowerTest.powerTestParams.min_power + " FLOAT NOT NULL"
                + ", " + PowerTest.powerTestParams.max_power + " FLOAT NOT NULL"
                + ", " + PowerTest.powerTestParams.actual_power + " FLOAT NOT NULL"
                + ", " + PowerTest.powerTestParams.test_status + " VARCHAR(255) NOT NULL"
                + ")";

        statement = dbConnection.prepareStatement(createTable);
        statement.execute();

        disconnectDb();
    }

    /**
     * Helper tool to clear the Power Table if I wanted to retest the inserts.
     * @throws SQLException
     */
    public void clearPowerTestTable() throws SQLException {

        clearTable(dbTable.power_test.toString());
    }

    /**
     * Reusable method when we have multiple tables working with.
     * @param table
     * @throws SQLException
     */
    private void clearTable(String table) throws SQLException {

        connectDb();
        String clearTable = "DELETE from " + table;

        statement = dbConnection.prepareStatement(clearTable);
        statement.execute();

        disconnectDb();
    }

    /**
     * Debug Tool in printing the ResultSet retrieved.
     * WARNING That this method goes through the ResultSet so user can't go back to it.
     * @param rs
     * @throws SQLException
     */
    private void prettyPrintRs(ResultSet rs) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();
        int colCount = metaData.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= colCount; i++) {
                String colValue = rs.getString(i);
                System.out.print(metaData.getColumnName(i) + ": " + colValue + ". ");
            }
            System.out.println("");
        }
    }

    /**
     * DB Method to insert Test Results.
     * @param pt PowerTest Instance
     * @throws SQLException
     */
    public void savePowerTestResultToDb(PowerTest pt) throws SQLException {
        connectDb();

        String query = "INSERT INTO " + dbTable.power_test + "("
                + PowerTest.powerTestParams.test_time
                + ", " + PowerTest.powerTestParams.min_power
                + ", " + PowerTest.powerTestParams.max_power
                + ", " + PowerTest.powerTestParams.actual_power
                + ", " + PowerTest.powerTestParams.test_status
                + ")"
                + "VALUES (?,?,?,?,?)";

        statement = dbConnection.prepareStatement(query);
        statement.setLong(1, pt.getTestTime());
        statement.setFloat(2, pt.getMinPower());
        statement.setFloat(3, pt.getMaxPower());
        statement.setFloat(4, pt.getActualPower());
        statement.setString(5, pt.getTestStatus());
        statement.execute();

        disconnectDb();
    }

    /**
     * Exports the Power Test Table into a CSV File Raw(Timestamp in DB is in unix time format)
     * @param targetLocation Target path for CSV.
     * @throws SQLException
     * @throws IOException
     */
    public void exportToCsvRaw(String targetLocation) throws SQLException, IOException {
        exportToCsvFile(false, targetLocation);
    }

    /**
     * Exports the Power Test Table into a CSV File with the timestamp in DATE format. In DB the timestamp is
     * in unix time format
     * @param targetLoc Target Path for CSV
     * @throws SQLException
     * @throws IOException
     */
    public void exportToCsvPretty(String targetLoc) throws SQLException, IOException {
        exportToCsvFile(true, targetLoc);
    }

    /**
     * Main method in exporting the Power Test table into a CSV File.
     * @param prettyPrint
     * @param targetLocation Target Path for CSV
     * @throws SQLException
     * @throws IOException
     */
    private void exportToCsvFile(boolean prettyPrint, String targetLocation) throws SQLException, IOException {
        connectDb();

        String query = "SELECT * FROM " + dbTable.power_test;

        statement = dbConnection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        String fileName = "ExportedResults_" + System.currentTimeMillis();;

        BufferedWriter fileWriter = new BufferedWriter(
                new FileWriter(
                        targetLocation
                                + "/"
                                + fileName
                                + ".csv"));

        String headerText = PowerTest.powerTestParams.test_time.toString()
                + "," + PowerTest.powerTestParams.min_power.toString()
                + "," + PowerTest.powerTestParams.max_power.toString()
                + "," + PowerTest.powerTestParams.actual_power.toString()
                + "," + PowerTest.powerTestParams.test_status.toString()
                ;

        // First Row is the Headers
        fileWriter.write(headerText);

        while (rs.next()) {
            long testTime = rs.getLong(PowerTest.powerTestParams.test_time.toString());
            float minPower = rs.getFloat(PowerTest.powerTestParams.min_power.toString());
            float maxPower = rs.getFloat(PowerTest.powerTestParams.max_power.toString());
            float actualPower = rs.getFloat(PowerTest.powerTestParams.actual_power.toString());
            String testStatus = rs.getString(PowerTest.powerTestParams.test_status.toString());

            String line;

            if (prettyPrint){
                // Convert unix time format into a DATE format
                line = String.format("%s,%f,%f,%f,%s",
                        HelperTool.prettyPrint(testTime)
                        , minPower, maxPower, actualPower, testStatus);
            }
            else{

                line = String.format("%d,%f,%f,%f,%s",
                        testTime, minPower, maxPower, actualPower, testStatus);
            }

            fileWriter.newLine();
            fileWriter.write(line);
        }

        fileWriter.close();
        disconnectDb();
    }


    public enum dbTable {
        power_test
    }
}
