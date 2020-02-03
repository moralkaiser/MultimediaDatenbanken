package com.example.mmdb_layout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class ImportDBActivity extends AppCompatActivity {

    private DatabaseHelper myDbHelper = null;
    private Button buttonStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_db);

        buttonStart = (Button) findViewById(R.id.ok_button);

        buttonStart.setActivated(false);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToMainActivity();
            }
        });

        resetDatabase();

        buttonStart.setActivated(true);
    }

    public void goBackToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    void resetDatabase()
    {
        myDbHelper = new DatabaseHelper(this);
        try {
            myDbHelper.resetDBAnyway();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }
}
