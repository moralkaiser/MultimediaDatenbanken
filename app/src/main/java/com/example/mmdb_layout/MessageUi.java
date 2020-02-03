package com.example.mmdb_layout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import static com.example.mmdb_layout.R.array.occasion_array;


public class MessageUi extends AppCompatActivity {


    private EditText et;
    private Spinner spinner;

    // Spinner Array + custom layout
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_ui);
        spinner = (Spinner) findViewById(R.id.spinner);

        String[] items = new String[]{"Christmas", "Halloween", "Satanic New Year"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout_head, items);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_layout);


        // Clear Button
        et = (EditText) findViewById(R.id.message_area);
        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.getText().clear();
            }
        });
        // Okay Button
        Button okay = (Button) findViewById(R.id.okay);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMessageUI();
            }
        });


        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(250);
        et.setFilters(filterArray);
    }
    public void OpenMessageUI (){
        Intent intent = new Intent(this,ResultView.class);

        intent.putExtra("message", et.getText().toString());
        intent.putExtra("occID", spinner.getSelectedItemPosition()+1);
        startActivity(intent);
    }
};

