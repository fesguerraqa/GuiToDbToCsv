package gui.testrunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;

public class PowerReadTest extends JFrame{
    private JPanel jPnlMain;
    private JTextField txtFldMinValue;
    private JTextField txtFldMaxValue;
    private JTextField txtFldResult;
    private JButton bttnRunSimTest;
    private JButton bttnSaveToDb;
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

    private int myMin;
    private int myMax;
    private int myResult;


    public static void main(String[] args) throws SQLException, IOException {

        new PowerReadTest();
//        DbWorker dw = new DbWorker();
//        dw.readDbContents();

    }

    public PowerReadTest(){

        //txtFldResult.setEditable(false);

        setTitle("Power Capture Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(jPnlMain);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        disableDbButton();

        activateMouseListenersOnTxtFields();

        simulateTest();
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

                updateTestStatus("Entering expected MIN value on GUI...");
            }
        });
        txtFldMaxValue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateTestStatus("Entering expected MAX Value on GUI...");
            }
        });
        txtFldResult.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateTestStatus("Entering Test Result on GUI...");
            }
        });
    }

    private void refreshValues(){

        this.myMin = Integer.parseInt(txtFldMinValue.getText());
        this.myMax = Integer.parseInt(txtFldMaxValue.getText());
        this.myResult = Integer.parseInt(txtFldResult.getText());
    }

    private void showErrorMessageBox(String s){
        int result = JOptionPane.showConfirmDialog(this
                , s
                , "INVALID INPUT"
                , JOptionPane.OK_OPTION
        //        , JOptionPane.QUESTION_MESSAGE
        );

        updateTestStatus(s);
    }

    private boolean returnIfInputValuesAreValid() {

        boolean inputValuesValid = true;

        try {
            refreshValues();

            if (myMin > myMax) {

                String errMinMax = "INVALID: MIN CANNOT be greater than MAX";

                inputValuesValid = false;
                disableDbButton();
                showErrorMessageBox(errMinMax);
                //quickConfirmDialog(errMinMax);
            }
            else if (myMin == myMax) {

                String errEqual = "INVALID: MIN and MAX Cannot be EQUAL";
                disableDbButton();
                showErrorMessageBox(errEqual);
                inputValuesValid = false;
            }
        }
        catch (Exception ex){
            String exception = ex.getMessage();
            showErrorMessageBox("Invalid INPUT FOUND: " + exception);
        }

        return inputValuesValid;
    }

    private void quickConfirmDialog(String s){
        JOptionPane.showConfirmDialog(this, s);
    }

    private void enableDbButton(){
        bttnSaveToDb.setVisible(true);
    }

    private void disableDbButton(){
        bttnSaveToDb.setVisible(false);
    }

    private void simulateTest(){

        bttnRunSimTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean inputsAreValid = returnIfInputValuesAreValid();

                if (inputsAreValid) {

                    processTest();

                    saveOrRerunTest();
                }
            };
        });
    }

    private void saveOrRerunTest(){
        String testStatus = "NOWHERE LAND";

        // TODO: Research the Frame.getFrames more.
        int result = JOptionPane.showConfirmDialog(Frame.getFrames()[0]
                , "Save the results in the database?\n\nYES to Save.\nNO to rerun test."
                , "Test Completed",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION){
            saveResultsToDb();
            testStatus = "Saved in DB!";
        }else if (result == JOptionPane.NO_OPTION){
            testStatus = "Lets do it again";
        }

        updateTestStatus(testStatus);
    }

    private void saveResultsToDb() {

    }

    private void updateTestStatus(String s){
        lblCurrentStatus.setText(s);
    }


    private boolean processTest() {

        refreshValues();

        String testStatus = "PASS";

        boolean inRange = true;

        if (myResult < myMin || myResult > myMax){
            inRange = false;
            testStatus = "FAILED";
        }

        updateTestStatus(testStatus);
        return inRange;
    }
}
