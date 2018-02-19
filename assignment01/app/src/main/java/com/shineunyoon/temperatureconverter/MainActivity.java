package com.shineunyoon.temperatureconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    private TextView history;
    private ScrollView container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // History
        history = (TextView)findViewById(R.id.history);
        container = (ScrollView)findViewById(R.id.historyBox);
    }

    public void doConversion(View v) {

        // Radio Button Group
        RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup);
        RadioButton conversion1 = (RadioButton)findViewById(R.id.choice1);
        RadioButton conversion2 = (RadioButton)findViewById(R.id.choice2);


        // Get Components to Convert
        EditText v1 = findViewById(R.id.btemp);
        double input = Double.parseDouble(v1.getText().toString());
        double output;
        TextView answerText = findViewById(R.id.atemp);

        // For Managing history
        String newText;
        String historyText = history.getText().toString();

        // 1. Conversion (F to C)
        if(conversion1.isChecked()) {
            output = ((input - 32.0) * 5.0)/9.0;
            output = Math.round(output*10)/10.0;
            newText = "F to C: " + input + " -> " + output;
        }

        // 2. Conversion (C to F)
        else {
            output = ((input * 9.0)/5.0) + 32.0;
            output = Math.round(output*10)/10.0;
            newText = "C to F: " + input + " -> " + output;
        }

        // Show the result of conversion
        answerText.setText("" + output);

        // Add to history
        history.setText(newText + "\n" + historyText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("HISTORY", history.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        history.setText(savedInstanceState.getString("HISTORY"));
    }
}
