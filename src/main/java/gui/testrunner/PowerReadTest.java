package gui.testrunner;

import db.DbWorker;
import db.PowerTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;

public class PowerReadTest extends JFrame{
    private JPanel jPnlMain;
    private JTextField txtFldMinValue;
    private JTextField txtFldMaxValue;
    private JTextField txtFldResult;
    private JButton bttnRunSimTest;
    private JButton bttnExportToCsv;
    private JLabel lblCurrentStatus;
    private JPanel pnlMinVal;
    private JPanel pnlMaxValue;
    private JPanel pnlTestResult;
    private JPanel pnlStatus;
    private JPanel pnlButtons;
    private JLabel lblMinValue;
    private JLabel lblMaxValue;
    private JLabel lblResult;
    private JLabel lblStatusLabel;

    private float myMinPower;
    private float myMaxPower;
    private float myActualPower;


    public static void main(String[] args) throws SQLException, IOException {

        new PowerReadTest();

    }

    public PowerReadTest(){

        setTitle("Power Capture Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(jPnlMain);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        activateMouseListenersOnTxtFields();
        activateMouseListenerForDbToCsvExport();

        launchPowerCaptureTest();
    }

    /**
     * Mouse listener so initiate exporting the DB contents into a CSV file.
     */
    public void activateMouseListenerForDbToCsvExport(){
        bttnExportToCsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DbWorker dw = new DbWorker();

                try {

                    String location = targetDirectoryForCsv();

                    if (!location.isEmpty()) {
                        dw.exportToCsvPretty(location);

                        updateTestStatusOnGui("CSV file created in: " + location);
                    } else {
                        updateTestStatusOnGui("Export Cancelled.");
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    /**
     * We launch a Path Chooser to have the user select the target location of the CSV file.
     * @return Target Directory for the CSV file to be saved at.
     */
    private String targetDirectoryForCsv(){

        String targetPath = "";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(PowerReadTest.this);
        if(option == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            targetPath = file.getAbsolutePath();
            System.out.println(targetPath);
        }
        else{
        }

        return targetPath;
    }

    /**
     * This method contains adding Mouse Clicked Event listeners to all the Input Text Fields.
     * There is text on the GUI displaying what the current user is doing with the GUI.
     */
    private void activateMouseListenersOnTxtFields(){
        txtFldMinValue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                updateTestStatusOnGui("Entering expected MIN value on GUI...");
            }
        });
        txtFldMaxValue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateTestStatusOnGui("Entering expected MAX Value on GUI...");
            }
        });
        txtFldResult.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateTestStatusOnGui("Simulating a Results Capture on GUI...");
            }
        });
    }

    /**
     * Read values from the GUI and put in local variables.
     */
    private void refreshValues(){

        this.myMinPower = Float.parseFloat(txtFldMinValue.getText());
        this.myMaxPower = Float.parseFloat(txtFldMaxValue.getText());
        this.myActualPower = Float.parseFloat(txtFldResult.getText());
    }

    private void showErrorMessageBox(String s){
        int result = JOptionPane.showConfirmDialog(this
                , s
                , "INVALID INPUT"
                , JOptionPane.PLAIN_MESSAGE
        );

        updateTestStatusOnGui(s);
    }

    /**
     * Focus of method is to check the following:
     * - Correct decimal inputs for Min, Max and Result
     * - Min should be less than Max
     * - Min should not be equal to Max(TODO: There can be an arguement that this can be allowed)
     * @return
     */
    private boolean checkForValidInputs(PowerTest pt) {

        boolean inputValuesValid = true;

        if (pt.getTestStatus().equals(PowerTest.testStatusEnum.INVALID.toString())){
            inputValuesValid = false;
            showErrorMessageBox(pt.getTestStatusVerbose());
        }

        return inputValuesValid;
    }

    private void quickConfirmDialog(String s){
        JOptionPane.showConfirmDialog(this, s);
    }

    private void enableDbButton(){
        bttnExportToCsv.setVisible(true);
    }

    private void disableDbButton(){
        bttnExportToCsv.setVisible(false);
    }

    private void launchPowerCaptureTest(){

        bttnRunSimTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try{
                    refreshValues();

                    PowerTest pt = new PowerTest(
                            new Date().getTime()
                            , myMinPower
                            , myMaxPower
                            , myActualPower
                    );


                    if (!pt.getTestStatus().equals(PowerTest.testStatusEnum.INVALID.toString())) {

                        try {
                            saveOrRerunTest( pt);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    else{
                        showErrorMessageBox(pt.getTestStatusVerbose());
                    }

                }
                catch (Exception ex){
                    showErrorMessageBox("EXCEPTION: " + ex.getMessage());
                }
            };
        });
    }

    private void saveOrRerunTest(PowerTest pt) throws SQLException {

        String testStatus = pt.getTestStatus();

        updateTestStatusOnGui(testStatus);

        // TODO: Research the Frame.getFrames more.
        int result = JOptionPane.showConfirmDialog(Frame.getFrames()[0]
                , "Save the results in the database?\n\nYES to Save.\nNO to rerun test."
                , "Test Complete: " + testStatus,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION){

            DbWorker dw = new DbWorker();
            dw.savePowerTestResultToDb(pt);

            testStatus = "Saved in DB!";
        }else if (result == JOptionPane.NO_OPTION){
            testStatus = "Lets do it again!";
        }

        updateTestStatusOnGui(testStatus);
    }


    private void updateTestStatusOnGui(String s){
        lblCurrentStatus.setText(s);
    }


    /**
     * Focus of this method is to check for test logic.
     * @return
     */
    private boolean processTest() {

        refreshValues();

        boolean inRange = true;

        if (myActualPower < myMinPower || myActualPower > myMaxPower){
            inRange = false;
        }

        return inRange;
    }
}
