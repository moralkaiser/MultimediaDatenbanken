package com.example.mmdb_layout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class CardView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        CardLogic cardLogic = new CardLogic(this);

        ImageView resultView = (ImageView)findViewById(R.id.cardView);
        byte[] screenshotByteArray = getIntent().getByteArrayExtra("card");
        resultView.setImageBitmap(cardLogic.convertBlobToBitmap(screenshotByteArray));
    }
}
