package com.example.james.tipster;


import android.content.Intent;
import android.widget.EditText;

public class NumberPickerLogic {
    int minium = 1;
    int maximun = Integer.MAX_VALUE;
    EditText number;

    public NumberPickerLogic(EditText number){ this(number, 0, Integer.MAX_VALUE);}

    public NumberPickerLogic(EditText number, int minium, int maximun){
        super();
        this.number = number;
        this.minium = minium;
        this.maximun = maximun;
    }

    public void increment(){
        final int newValue = clamp(getValue() + 1);
        setValue(newValue);
    }

    public void decrement(){
        final int newValue = clamp(getValue() - 1);
        setValue(newValue);
    }

    int clamp(int newValue){
        if(newValue < minium){
            newValue =minium;
        }
        if(newValue> maximun){
            newValue = maximun;
        }
        return newValue;
    }

    public int getValue(){return Integer.parseInt(number.getText().toString());}

    public void setValue(int value){number.setText(Integer.toString(value));}
}
