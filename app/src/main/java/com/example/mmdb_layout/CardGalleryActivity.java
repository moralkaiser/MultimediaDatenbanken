package com.example.mmdb_layout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class CardGalleryActivity extends AppCompatActivity {


    private DatabaseHelper myDbHelper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_gallery);

        loadDatabase();

        ListView listView = (ListView) findViewById(R.id.listview_activity_main);

        final ArrayList<String> cardNames = getCardNames();

//Create Adapter
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,cardNames);

//assign adapter to listview
        listView.setAdapter(arrayAdapter);

//add listener to listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(CardGalleryActivity.this,"clicked item:"+i+" "+cardNames.get(i).toString(),Toast.LENGTH_SHORT).show();
                gotoCardView(i+1);
            }
        });

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

    public void gotoCardView (int cardIndex)
    {
        Intent intent = new Intent(this,CardView.class);

        intent.putExtra("card", getCard(cardIndex));

        startActivity(intent);
    }

    ArrayList<String> getCardNames()
    {
        ArrayList<String> returnList = new ArrayList<String>();

        Cursor dbContent = myDbHelper.query("SELECT * FROM CARD");

        if (dbContent.moveToFirst())
        {
            do {

                returnList.add(dbContent.getString(1));

            } while (dbContent.moveToNext());
        }

        return returnList;
    }

    byte[] getCard(int cardID)
    {
        byte[] returnBlob = null;

        Cursor dbContent = myDbHelper.query("SELECT * FROM CARD WHERE CardID="+cardID);

        if (dbContent.moveToFirst())
        {
            do {

                returnBlob = dbContent.getBlob(2);

            } while (dbContent.moveToNext());
        }

        return returnBlob;
    }
}
