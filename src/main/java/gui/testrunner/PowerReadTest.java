package gui.testrunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class PowerReadTest extends JFrame{
    private JPanel jPnlMain;
    private JTextField txtFldMinValue;
    private JTextField txtFldMaxValue;
    private JTextField txtFldResult;
    private JButton bttnRunSimTest;
    private JButton bttnSaveToDb;
    private JLabel lblPassOrFail;

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
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        disableDbButton();


        simulateTest();
    }

    private void refreshValues(){
        this.myMin = Integer.parseInt(txtFldMinValue.getText());
        this.myMax = Integer.parseInt(txtFldMaxValue.getText());
        this.myResult = Integer.parseInt(txtFldResult.getText());

    }

    private boolean checkForValidMinMax() {

        boolean isValid = true;

        try {
            refreshValues();


            if (myMin > myMax) {
                lblPassOrFail.setText("INVALID: MIN Input CANNOT be greater than MAX");
                isValid = false;
                disableDbButton();
            } else if (myMin == myMax) {
                lblPassOrFail.setText("INVALID: MIN AND MAX Cannot be EQUAL");
                isValid = false;
                disableDbButton();
            }
        }
        catch (Exception ex){
            lblPassOrFail.setText("SOMETHING FUNKY with INPUTS. ONLY Values only");
        }

        return isValid;


    }

    private void clearLabel(){

        lblPassOrFail.setText("Editing... Please Complete Input");
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

                boolean minMaxValid = checkForValidMinMax();

                if (minMaxValid) {

                    boolean withinRange = isWithinRange();

                    setLabelText(withinRange);
                }
            }


;        });


    }
    private boolean isWithinRange() {

        refreshValues();

        boolean inRange = true;

        if (myResult < myMin || myResult > myMax){
            inRange = false;
        }

        return inRange;
    }
    private void setLabelText(boolean withinRange) {

        String finalTxt = "FAILED";

        if (withinRange){
            finalTxt = "PASS";
        }

        lblPassOrFail.setText(finalTxt);
        enableDbButton();
    }
}
