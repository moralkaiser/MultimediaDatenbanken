package com.example.mmdb_layout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button buttonStart;
    private Button buttonImportDB;
    private Button buttonGalery;
    private DatabaseHelper myDbHelper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = (Button) findViewById(R.id.start_button);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMessageUI();
            }
        });

        buttonImportDB = (Button) findViewById(R.id.importDB_button);
        buttonImportDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoImportDBActivity();
            }
        });

        buttonGalery = (Button) findViewById(R.id.galery_button);
        buttonGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoGalery();
            }
        });

        loadDatabase();

        Log.d("CARDS",myDbHelper.getCardCount()+"");
    }

    public void openMessageUI(){
        Intent intent = new Intent(this, MessageUi.class);
        startActivity(intent);
    }

    public void gotoImportDBActivity(){
        Intent intent = new Intent(this, ImportDBActivity.class);
        startActivity(intent);
    }

    public void gotoGalery(){
        Intent intent = new Intent(this, CardGalleryActivity.class);
        startActivity(intent);
    }

    void loadDatabase()
    {
        myDbHelper = new DatabaseHelper(MainActivity.this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    void resetDatabase()
    {
        myDbHelper = new DatabaseHelper(MainActivity.this);
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
