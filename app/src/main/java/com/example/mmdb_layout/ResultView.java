package com.example.mmdb_layout;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ResultView extends AppCompatActivity {

    private CardLogic cardLogic = null;
    private ArrayList<ImageView> clipartSpaces = new ArrayList<ImageView>();
    private ArrayList<Bitmap> cliparts = new ArrayList<Bitmap>();
    private TextView headline = null;
    private ImageView background = null;
    private Button viewdata = null;
    private Button clearButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_view);

        final int occasionID = getIntent().getIntExtra("occID",1);
        final String message = getIntent().getStringExtra("message");

        viewdata = (Button) findViewById(R.id.okay2);
        viewdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMessageUI(message,occasionID);
            }
        });

        clearButton = (Button) findViewById(R.id.clear2);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getKeywordBitmapsAndShare(message,occasionID);
            }
        });



        cardLogic = new CardLogic(this);

        background = (ImageView) findViewById(R.id.background);
        ImageView border = (ImageView) findViewById(R.id.border);

        background.setImageBitmap(cardLogic.loadOccasionBackground(occasionID));
        border.setImageBitmap(cardLogic.loadOccasionBorder(occasionID));

        TextView messageContainer = (TextView) findViewById(R.id.textView);

        messageContainer.setText(message);


        clipartSpaces.add((ImageView)findViewById(R.id.clipart_left_top));
        clipartSpaces.add((ImageView)findViewById(R.id.clipart_left_middle));
        clipartSpaces.add((ImageView)findViewById(R.id.clipart_left_bottom));
        clipartSpaces.add((ImageView)findViewById(R.id.clipart_right_top));
        clipartSpaces.add((ImageView)findViewById(R.id.clipart_right_middle));
        clipartSpaces.add((ImageView)findViewById(R.id.clipart_right_bottom));

        headline = (TextView) findViewById(R.id.headline);
        headline.setText(cardLogic.getHeadline(occasionID));

        String fontColor = cardLogic.getFontColor(occasionID);
        messageContainer.setTextColor(Color.parseColor(fontColor));
        headline.setTextColor(Color.parseColor(fontColor));

        getKeywordBitmapsAndShare(message, occasionID);


    }
    public void OpenMessageUI (String message, int occID)
    {
        Bitmap screenshot = snapScreen();

        int height = screenshot.getHeight();
        int width = screenshot.getWidth();

        Log.d("SCREENL","Height: " + height +" | Width: "+width);

        int topcutoff = (int)((75/1080.0)*width);
        int bottomcutoff = (int)((300/1080.0)*width);

        height = height - topcutoff;
        height = height - bottomcutoff;
        screenshot = Bitmap.createBitmap(screenshot, 0, topcutoff, width, height);


        Intent intent = new Intent(this,DownloadView.class);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        screenshot.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        byte[] byteArray = stream.toByteArray();

        intent.putExtra("card",byteArray);
        intent.putExtra("occID",occID);
        intent.putExtra("message",message);

        startActivity(intent);
    }


    public Bitmap takeScreenShot(View view) {
        // configuramos para que la view almacene la cache en una imagen
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        view.buildDrawingCache();

        if(view.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

        // utilizamos esa cache, para crear el bitmap que tendra la imagen de la view actual
        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }

    Bitmap snapScreen()
    {
        Bitmap returnBitmap;

        viewdata.setVisibility(View.INVISIBLE);
        clearButton.setVisibility(View.INVISIBLE);
        View v1 = getWindow().getDecorView().getRootView();

        returnBitmap = takeScreenShot(v1);

        viewdata.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);

        return returnBitmap;
    }

    void getKeywordBitmapsAndShare(String message, int occID)
    {
        cliparts.clear();
        cliparts = cardLogic.getAllBitmapsToProsaText(message,occID);

        shareBitmaps();
    }

    void shareBitmaps()
    {
        ArrayList<Bitmap> tmpBitmapList = new ArrayList<Bitmap>(cliparts);

        for(int counter=0; counter < 6; counter++)
        {
            Bitmap bitmap = cutRandom(tmpBitmapList);

            clipartSpaces.get(counter).setImageBitmap(bitmap);
        }

        ArrayList<String> synonyms = ThesaurusAPICommunicator.getSynonyms("car",this);
        Log.d("SYNONYMS",synonyms.size()+"");
    }


    Bitmap cutRandom(ArrayList<Bitmap> bitmapList)
    {
        Bitmap returnBitmap;
        int randomIndexBitmapList=(int)(Math.random()*bitmapList.size());

        returnBitmap = bitmapList.get(randomIndexBitmapList);
        bitmapList.remove(randomIndexBitmapList);

        return returnBitmap;
    }



}
