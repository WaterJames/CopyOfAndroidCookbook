package com.example.james.tipster;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends Activity {

    final static int DEFAULT_NUM_PEOPLE = 3;

    final  static NumberFormat formatter = NumberFormat.getCurrencyInstance();

    //Widgets小部件 in the application
    private EditText txtAmount;
    private EditText txtPeople;
    private EditText txtTipOther;
    private RadioGroup rdoGroupTips;
    private Button btnCalculate;
    private Button btnReset;

    private TextView txtTipAmount;
    private TextView txtTotalToPay;
    private TextView txtTipPerPerson;

    //For the if of radio button selected
    private int radioCheckedId = -1;
    private NumberPickerLogic mLogic;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access the various widgets by their id in R.java
        txtAmount = (EditText) findViewById(R.id.txtAmount);
        //On app load, the cursor should be in the Amount field
        txtAmount.requestFocus();

        txtPeople = (EditText) findViewById(R.id.txtPeople);
        txtPeople.setText(Integer.toString(DEFAULT_NUM_PEOPLE));

        txtTipOther = (EditText) findViewById(R.id.txtTipOther);

        rdoGroupTips = (RadioGroup) findViewById(R.id.RadioGroupTips);

        btnCalculate = (Button) findViewById(R.id.btnCalculate);
        //On app load, the Calculate button is disabled
        btnCalculate.setEnabled(false);

        btnReset = (Button) findViewById(R.id.btnReset);

        txtTipAmount = (TextView) findViewById(R.id.txtTipAmout);
        txtTotalToPay = (TextView) findViewById(R.id.txtTotalToPay);
        txtTipPerPerson = (TextView) findViewById(R.id.txtTipPerPerson);

        // On app load, disable the 'Other tip' percentage text field
        txtTipOther.setEnabled(false);

        rdoGroupTips.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Enable/disable Other Percentage tip field
                if(checkedId == R.id.radioFifteen || checkedId == R.id.radioTwenty){
                    txtTipOther.setEnabled(false);
                    /*
                     * Enable the calculate button if Total Amount and No. of
                     * People fields have valid values.
                     */
                    btnCalculate.setEnabled((txtAmount.getText().length() > 0 && txtPeople.getText().length() > 0));
                }

                if(checkedId == R.id.radioOther){
                    //enable the Other Percentage tip field
                    txtTipOther.setEnabled(true);
                    //set the focus to this field
                    txtTipOther.requestFocus();

                    /*
                     * Enable the calculate button if Total Amount and No. of
                     * People fields have valid values. Also ensure that user
                     * has entered a Other Tip Percentage value before enabling
                     * the Calculate button.
                     */
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0
                            && txtPeople.getText().length() > 0
                            && txtTipOther.getText().length() > 0);
                }
                //To determine the tip percentage choice made by user
                radioCheckedId = checkedId;
            }
        });

        /*
         * Attach a KeyListener to the Tip Amount, No. of People and Other Tip
         * Percentage text fields
         */
        txtAmount.setOnKeyListener(mKeyListener);
        txtPeople.setOnKeyListener(mKeyListener);
        txtTipOther.setOnKeyListener(mKeyListener);

        btnCalculate.setOnClickListener(mClickListener);
        btnReset.setOnClickListener(mClickListener);

        /** Create a NumberPickerLogic to handle the + and - keys */
        mLogic = new NumberPickerLogic(txtPeople, 1, Integer.MAX_VALUE);
    }

    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            switch (v.getId()){
                case R.id.txtAmount:
                case R.id.txtPeople:
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 && txtPeople.getText().length() > 0);
                    break;
                case R.id.txtTipOther:
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 && txtPeople.getText().length() > 0 && txtTipOther.getText().length() > 0);
                    break;
            }
            return false;
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnCalculate){
                calculate();
            }else{
                reset();
            }
        }
    };

    /**
     * Calculate the tip as per data entered by the user.
     */
    private void calculate(){
        Double billAmount = Double.parseDouble(txtAmount.getText().toString());
        Double totalPeople = Double.parseDouble(txtPeople.getText().toString());
        Double percentage = null;

        boolean isError = false;
        if(billAmount < 1.0){
            showErrorAlert("Enter a valid Total Amount.", txtAmount.getId());
            isError = true;
        }
        if(totalPeople < 1.0){
            showErrorAlert("Enter a valid number of people.",
                    txtPeople.getId());
            isError = true;
        }

        /*
         * If user never changes radio selection, then it means
         * the default selection of 15% is in effect. But it's
         * safer to verify...
         */
        if(radioCheckedId == -1){
            //默认-1表示选中第一个，即15%
            radioCheckedId = rdoGroupTips.getCheckedRadioButtonId();
        }
        if(radioCheckedId == R.id.radioFifteen){
            percentage = 15.00;
        }else if(radioCheckedId == R.id.radioTwenty){
            percentage = 20.00;
        }else if(radioCheckedId == R.id.radioOther){
            percentage = Double.parseDouble(
                    txtTipOther.getText().toString());
            if(percentage < 1.0){
                showErrorAlert("Ener a valid Tip percentage", txtTipOther.getId());
                isError = true;
            }
        }
        /*
         * If all fields are populated with valid values, then proceed to
         * calculate the tips
         */
        if(!isError){
            double tipAmount = ((billAmount * percentage) / 100);
            double totalToPay = billAmount + tipAmount;
            double perPersonPays = totalToPay / totalPeople;

            txtTipAmount.setText(formatter.format(tipAmount));
            txtTotalToPay.setText(formatter.format(totalToPay));
            txtTipPerPerson.setText(formatter.format(perPersonPays));
        }
    }

    private void reset(){
        txtTipAmount.setText("");
        txtTotalToPay.setText("");
        txtTipPerPerson.setText("");
        txtAmount.setText("");
        txtPeople.setText(Integer.toString(DEFAULT_NUM_PEOPLE));
        txtTipOther.setText("");
        rdoGroupTips.clearCheck();
        rdoGroupTips.check(R.id.radioFifteen);
        // set focus on the first field
        txtAmount.requestFocus();
    }

    public void decrement(View v) {
        mLogic.decrement();
    }

    public void increment(View v) {
        mLogic.increment();
    }

    /**
     * Shows the error message in an alert dialog
     *
     * @param errorMessage
     *            String the error message to show
     * @param fieldId
     *            the Id of the field which caused the error.
     *            This is required so that the focus can be
     *            set on that field once the dialog is
     *            dismissed.
     */
    private void showErrorAlert(String errorMessage,final int fieldId ){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setNeutralButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                findViewById(fieldId).requestFocus();
                            }
                        }).show();
    }

}
