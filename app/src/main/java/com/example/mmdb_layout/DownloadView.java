package com.example.mmdb_layout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class DownloadView extends AppCompatActivity {

    byte[] screenshotByteArray = null;
    private DatabaseHelper myDbHelper = null;
    int occID = 0;
    String message = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_view);

        CardLogic cardLogic = new CardLogic(this);

        Button down = (Button) findViewById(R.id.download);
        Button back = (Button) findViewById(R.id.newcard);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMessageUI();
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
                OpenMessageUI();
            }
        });

        ImageView resultView = (ImageView)findViewById(R.id.resultView);
        screenshotByteArray = getIntent().getByteArrayExtra("card");
        resultView.setImageBitmap(cardLogic.convertBlobToBitmap(screenshotByteArray));

        occID = getIntent().getIntExtra("occID",0);
        message = getIntent().getStringExtra("message");

        loadDatabase();
    }

    void loadDatabase()
    {
        myDbHelper = new DatabaseHelper(this);
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

    public void OpenMessageUI (){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    void saveImage()
    {
        myDbHelper.saveCard(screenshotByteArray,occID,message);
    }
}
