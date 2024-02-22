package db;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
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
     * DB method to create a CSV File Table. This is only used during setup.
     * @throws SQLException
     */
    public void createCsvFileTable() throws SQLException {

        connectDb();

        String createTable = "CREATE TABLE " + dbTable.csv_file
                + "(" + CsvFile.csvFileParam.import_time + " BIGINT NOT NULL"
                + ", " + CsvFile.csvFileParam.filename + " VARCHAR(255) NOT NULL"
                + ", " + CsvFile.csvFileParam.filepath + " VARCHAR(255) NOT NULL"
                + ", " + CsvFile.csvFileParam.md5sum + " VARCHAR(255) NOT NULL"
                + ")";

        statement = dbConnection.prepareStatement(createTable);
        statement.execute();

        disconnectDb();
    }

    /**
     * DB method to create a CSV File Table. This is only used during setup.
     * @throws SQLException
     */
//    public void createAttenuationTestTable() throws SQLException {
//        connectDb();
//
//        String createTable = "CREATE TABLE " + csvToDbTable.attenuation_test
//                + "(" + AttenuationTest.attenTestParam.run_time + " INTEGER NOT NULL"
//                + ", " + AttenuationTest.attenTestParam.test_bench_id + " INTEGER NOT NULL"
//                + ", " + AttenuationTest.attenTestParam.serial_number + " INTEGER NOT NULL"
//                + ", " + AttenuationTest.attenTestParam.tx_power + " FLOAT NOT NULL"
//                + ", " + AttenuationTest.attenTestParam.rx_power + " FLOAT NOT NULL"
//                + ", " + AttenuationTest.attenTestParam.ceiling_pass + " FLOAT NOT NULL"
//                + ", " + AttenuationTest.attenTestParam.floor_pass + " FLOAT NOT NULL"
//                + ", " + AttenuationTest.attenTestParam.test_status + " VARCHAR(255) NOT NULL"
//                + ")";
//
//        statement = dbConnection.prepareStatement(createTable);
//        statement.execute();
//
//        disconnectDb();
//    }

    /**
     * Heloer tool to clear the AttenuationTest Table if I wanted to retest the inserts.
     * @throws SQLException
     */
    public void clearAttenuationTestTable() throws SQLException {

        clearTable(dbTable.attenuation_test.toString());
    }

    /**
     * Heloer tool to clear the CsvFile Table if I wanted to retest the inserts.
     * @throws SQLException
     */
    public void clearCsvFileTable() throws SQLException {

        clearTable(dbTable.csv_file.toString());
    }

    private void clearTable(String table) throws SQLException {

        connectDb();
        String clearTable = "DELETE from " + table;

        statement = dbConnection.prepareStatement(clearTable);
        statement.execute();

        disconnectDb();
    }

    /**
     * DB Method to insert a row of the Attenuation Test from the CSV File.
     * @param at Attenuation Test
     * @throws SQLException
     */
//    public void insertAttenuationTest(AttenuationTest at) throws SQLException {
//
//        connectDb();
//
//        String query = "INSERT INTO " + csvToDbTable.attenuation_test + "("
//                + AttenuationTest.attenTestParam.run_time
//                + ", " + AttenuationTest.attenTestParam.test_bench_id
//                + ", " + AttenuationTest.attenTestParam.serial_number
//                + ", " + AttenuationTest.attenTestParam.tx_power
//                + ", " + AttenuationTest.attenTestParam.rx_power
//                + ", " + AttenuationTest.attenTestParam.ceiling_pass
//                + ", " + AttenuationTest.attenTestParam.floor_pass
//                + ", " + AttenuationTest.attenTestParam.test_status
//                + ")"
//                + "VALUES (?,?,?,?,?,?,?,?)";
//
//        statement = dbConnection.prepareStatement(query);
//        statement.setInt(1, at.getRunTime());
//        statement.setInt(2, at.getTestBench());
//        statement.setInt(3, at.getSerialNumber());
//        statement.setFloat(4, at.getTxPower());
//        statement.setFloat(5, at.getRxPower());
//        statement.setFloat(6, at.getCeilingPass());
//        statement.setFloat(7, at.getFloorPass());
//        statement.setString(8, at.getTestStatus());
//        statement.execute();
//
//        disconnectDb();
//    }

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
     * DB Method to insert the details around the CSV file to track the history of its import.
     * @param cf
     * @throws SQLException
     */
    public void insertCsvFileData(CsvFile cf) throws SQLException {
        connectDb();

        String query = "INSERT INTO " + dbTable.csv_file + "("
                + CsvFile.csvFileParam.import_time
                + ", " + CsvFile.csvFileParam.filename
                + ", " + CsvFile.csvFileParam.filepath
                + ", " + CsvFile.csvFileParam.md5sum
                + ")"
                + "VALUES (?,?,?,?)";

        statement = dbConnection.prepareStatement(query);
        statement.setBigDecimal(1, cf.getImportTime());
        statement.setString(2, cf.getFilename());
        statement.setString(3, cf.getFilepath());
        statement.setString(4, cf.getMd5sum());
        statement.execute();

        disconnectDb();
    }

    public void readDbContents() throws SQLException, IOException {
        connectDb();

        String query = "SELECT * FROM " + dbTable.attenuation_test;

        statement = dbConnection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        String fileName = "ExportedResults_" + System.currentTimeMillis();;

        BufferedWriter fileWriter = new BufferedWriter(
                new FileWriter(
                        DIR_CSV
                                + fileName
                                + ".csv"));

        String headerText = attenTestParam.run_time.toString()
                + "," + attenTestParam.test_bench_id.toString()
                + "," + attenTestParam.serial_number.toString()
                + "," + attenTestParam.tx_power.toString()
                + "," + attenTestParam.rx_power.toString()
                + "," + attenTestParam.ceiling_pass.toString()
                + "," + attenTestParam.floor_pass.toString()
                + "," + attenTestParam.test_status.toString();

        // write header line containing column names
        fileWriter.write(headerText);

        while (rs.next()) {
            int runTime = rs.getInt(attenTestParam.run_time.toString());
            int testBench = rs.getInt(attenTestParam.test_bench_id.toString());
            int serialNum = rs.getInt(attenTestParam.serial_number.toString());

            // TODO Maybe just use INT for the floats that want int
            float txPower = rs.getFloat(attenTestParam.tx_power.toString());
            float rxPower = rs.getFloat(attenTestParam.rx_power.toString());
            float ceilingPass = rs.getFloat(attenTestParam.ceiling_pass.toString());
            float floorPass = rs.getFloat(attenTestParam.floor_pass.toString());
            String testStatus = rs.getString(attenTestParam.test_status.toString());


            String line = String.format("%d,%d,%d,%f,%f,%f,%f,%s",
                    runTime, testBench, serialNum, txPower, rxPower, ceilingPass, floorPass, testStatus);

            fileWriter.newLine();
            fileWriter.write(line);
        }

        fileWriter.close();
        disconnectDb();
    }

    public BigDecimal doesMd5sumExist(String md5sum) throws SQLException {
        connectDb();

        String query = "SELECT * FROM " + dbTable.csv_file
                + " WHERE " + CsvFile.csvFileParam.md5sum +  "=" + "\"" + md5sum + "\"";

        statement = dbConnection.prepareStatement(query);
        ResultSet result = statement.executeQuery();

        boolean hasResults = result.next();

        // We assign this key to tell the calling instance that if this doesn't get changed that the CSV file
        // has not been processed.
        BigDecimal tempTime = CsvFile.secretKey;

        if (hasResults) {

            // We will return the import time of the CSV File since it wa already imported.
            tempTime = result.getBigDecimal(CsvFile.csvFileParam.import_time.toString());
        }

        disconnectDb();

        return tempTime;
    }

    public enum dbTable {
        attenuation_test
        , csv_file
    }

    private String includeQuotes(String s){
        return "\"" + s + "\"";
    }

    public enum attenTestParam{
        run_time
        , test_bench_id
        , serial_number
        , tx_power
        , rx_power
        , ceiling_pass
        , floor_pass
        , test_status
    }

}
