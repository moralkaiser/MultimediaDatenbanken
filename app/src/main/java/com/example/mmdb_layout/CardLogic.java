package com.example.mmdb_layout;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class CardLogic {

    private DatabaseHelper myDbHelper = null;
    private Context context = null;

    public CardLogic(Context contextIn)
    {
        context = contextIn;

        loadDatabase();
    }

    void loadDatabase()
    {
        myDbHelper = new DatabaseHelper(context);
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

    String getFontColor(int occID)
    {
        String returnString = "";
        Cursor dbContent = myDbHelper.query("SELECT fontColor FROM OccasionTemplate WHERE OccasionTemplateID="+occID);

        if (dbContent.moveToFirst())
        {
            returnString = dbContent.getString(0);
        }
        return returnString;
    }

    String getHeadline(int occID)
    {
        String returnString = "";
        Cursor dbContent = myDbHelper.query("SELECT text FROM Headline WHERE HeadlineID="+occID);

        if (dbContent.moveToFirst())
        {
            returnString = dbContent.getString(0);
        }
        return returnString;
    }

    Bitmap resizeImage(Bitmap imgIn, int height)
    {
        double wTohRatioImg = imgIn.getWidth()/(imgIn.getHeight()*1.0);
        int newWidthImg = (int)Math.floor(wTohRatioImg*height);
        return Bitmap.createScaledBitmap(imgIn, newWidthImg, height, true);
    }

    Bitmap convertBlobToBitmap(byte[] blobImg)
    {
        return BitmapFactory.decodeByteArray(blobImg, 0 , blobImg.length);
    }

    Bitmap loadOccasionBackground(int occasionID)
    {
        Bitmap returnBitmap = null;

        if(occasionID != 3)
        {
            Cursor dbContent = myDbHelper.query("SELECT backgroundColor FROM OccasionTemplate WHERE occasionTemplateID="+occasionID);
            if (dbContent.moveToFirst())
            {
                returnBitmap = convertBlobToBitmap(dbContent.getBlob(0));
            }
        }
        else
        {
            Cursor dbContent = myDbHelper.query("SELECT outerborder FROM OccasionTemplate WHERE occasionTemplateID="+occasionID);
            if (dbContent.moveToFirst())
            {
                returnBitmap = convertBlobToBitmap(dbContent.getBlob(0));
            }
        }


        return returnBitmap;
    }

    Bitmap loadOccasionBorder(int occasionID)
    {
        Bitmap returnBitmap = null;

        if(occasionID != 3)
        {
            Cursor dbContent = myDbHelper.query("SELECT outerborder FROM OccasionTemplate WHERE occasionTemplateID="+occasionID);
            if (dbContent.moveToFirst())
            {
                returnBitmap = convertBlobToBitmap(dbContent.getBlob(0));
            }
        }
        else
        {

        }


        return returnBitmap;
    }

    boolean keyWordExists(String keyword, int occasionID)
    {
        Cursor dbContent = myDbHelper.query("SELECT * FROM OccasionKeyword WHERE text=\""+keyword+"\" AND occasionTemplateID="+occasionID);
        return dbContent.moveToFirst();
    }

    String[] splitProsaToWordArray(String prosa)
    {
        return prosa.split("[!? .]");
    }

    ArrayList<Bitmap> getBitmapsToOccasionKeyword(String keyword, int occasionID, ArrayList<Integer> clipartIDs)
    {

        ArrayList<Bitmap> returnBitmapList = new ArrayList<Bitmap>();

        Cursor dbContent = myDbHelper.query("SELECT Clipart.ClipartID, Clipart.image FROM OccasionKeyword INNER JOIN Clipart ON OccasionKeyword.clipartID=Clipart.ClipartID WHERE text=\""+keyword+"\" AND occasionTemplateID="+occasionID);

        if (dbContent.moveToFirst())
        {
            do {
                if(!clipartIDs.contains(dbContent.getInt(0)))
                {
                    clipartIDs.add(dbContent.getInt(0));
                    returnBitmapList.add(convertBlobToBitmap(dbContent.getBlob(1)));
                }

            } while (dbContent.moveToNext());
        }
        return returnBitmapList;
    }

    ArrayList<Bitmap> getAllBitmapsToProsaText(String prosa, int occasionID)
    {
        ArrayList<Integer> clipartIDs = new ArrayList<Integer>();
        ArrayList<Bitmap> returnAllLinkedBitmaps = new ArrayList<Bitmap>();

        String[] wordsInText = splitProsaToWordArray(prosa);

        for(String wordCaseSensitiv : wordsInText)
        {
            String word = wordCaseSensitiv.toLowerCase();

            if(keyWordExists(word,occasionID))
            {
                ArrayList<Bitmap> bitmapsToWord = getBitmapsToOccasionKeyword(word,occasionID,clipartIDs);

                for(Bitmap bitmap : bitmapsToWord)
                {
                    returnAllLinkedBitmaps.add(resizeImage(bitmap,200));
                }
            }
        }

        checkSize(occasionID,returnAllLinkedBitmaps,clipartIDs);

        for(int clipartID:clipartIDs)
        {
            Log.d("usedClipart",clipartID+"");
        }

        return returnAllLinkedBitmaps;
    }

    void checkSize(int occID,ArrayList<Bitmap> bitmapList ,ArrayList<Integer> clipartIDList)
    {
        if(bitmapList.size() < 6)
        {
            ArrayList<Bitmap> additionalBitmaps = pickRandomBitmaps(occID,6-bitmapList.size(),clipartIDList);

            for(Bitmap additionalBitmap : additionalBitmaps)
            {
                bitmapList.add(additionalBitmap);
            }
        }
    }

    ArrayList<Bitmap> pickRandomBitmaps(int occID, int count, ArrayList<Integer> clipartIDList)
    {
        ArrayList<Bitmap> returnBitmaps = new ArrayList<Bitmap>();

        Cursor dbContent = myDbHelper.query("SELECT Clipart.ClipartID FROM OccasionKeyword INNER JOIN Clipart ON OccasionKeyword.clipartID=Clipart.ClipartID WHERE occasionTemplateID="+occID);

        ArrayList<Integer> randomClipartIDs = new ArrayList<Integer>();

        if (dbContent.moveToFirst())
        {
            do {
                if(!randomClipartIDs.contains(dbContent.getInt(0)))
                {
                    randomClipartIDs.add(dbContent.getInt(0));
                }

            } while (dbContent.moveToNext());
        }

        for(int counter=0;counter < count;counter++)
        {
            int randomIndex=(int)(Math.random()*randomClipartIDs.size());

            while(clipartIDList.contains(randomClipartIDs.get(randomIndex)))
            {
                randomIndex=(int)(Math.random()*randomClipartIDs.size());
            }

            int randomClipartID = randomClipartIDs.get(randomIndex);

            clipartIDList.add(randomClipartID);

            returnBitmaps.add(getBitmapToClipartID(randomClipartID));
        }

        return returnBitmaps;
    }

    Bitmap getBitmapToClipartID(int clipartID)
    {
        Bitmap returnBitmap = null;

        Cursor dbContent = myDbHelper.query("SELECT image FROM Clipart WHERE ClipartID="+clipartID);

        if (dbContent.moveToFirst())
        {
            do {
                returnBitmap = convertBlobToBitmap(dbContent.getBlob(0));

            } while (dbContent.moveToNext());
        }
        return returnBitmap;
    }

}
