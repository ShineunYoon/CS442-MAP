package com.shineunyoon.assignment02;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.View;
import android.widget.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView savedTime;
    private EditText savedText;
    private Notepad notepad;
    private String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedTime = (TextView) findViewById(R.id.timestamp);
        savedTime.setText(currentTime);

        savedText = (EditText) findViewById(R.id.content);
        savedText.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onResume() {
        // Get the data from saved object
        notepad = loadData();
        savedTime.setText(notepad.getSavedTime());
        savedText.setText(notepad.getSavedText());
        super.onResume();
    }


    private Notepad loadData() {
        notepad = new Notepad();

        // Read data from JSON
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("timestamp")) {
                    notepad.setSavedTime(reader.nextString());
                } else if (name.equals("content")) {
                    notepad.setSavedText(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            // No data
        } catch (FileNotFoundException e) {
            notepad.setSavedTime(getCurrentTime());
            Toast.makeText(this, getString(R.string.welcome_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notepad;
    }


    @Override
    protected void onPause() {
        notepad.setSavedTime(savedTime.getText().toString());
        notepad.setSavedText(savedText.getText().toString());
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveNote();
        super.onStop();
    }

    private String getCurrentTime() {
        // Formatting timestamp
        Date date = new Date();
        // ex) Sat Feb 4, 10:15 AM
        SimpleDateFormat timeformat = new SimpleDateFormat("E MMM d, hh:mm a");
        currentTime = timeformat.format(date);

        return currentTime;
    }

    private void saveNote() {
        notepad.setSavedTime(getCurrentTime());

        try {
            FileOutputStream stream = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(stream, getString(R.string.encoding)));
            writer.setIndent("  ");

            writer.beginObject();
            writer.name("timestamp").value(notepad.getSavedTime());
            writer.name("content").value(notepad.getSavedText());
            writer.endObject();

            writer.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
