package com.example.mmdb_layout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;


public class DatabaseHelper extends SQLiteOpenHelper implements Serializable {

    String DB_PATH = null;
    private static String DB_NAME = "mydatabase";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 10);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);

    }


    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        Log.d("CREATEDB", "Exists: " + dbExist);
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    public void resetDBAnyway() throws IOException{
        this.getReadableDatabase();
        try {
            copyDataBase();
        } catch (IOException e) {
            throw new Error("Error copying database");
        }
    }

    public void resetDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        Log.d("CREATEDB", "Exists: " + dbExist);
        myContext.deleteDatabase("myDataBase");
        dbExist = checkDataBase();
        Log.d("CREATEDB", "Exists: " + dbExist);
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }


    public Cursor query(String sqlStmt)
    {
        return myDataBase.rawQuery(sqlStmt,null);
    }

    public int getCardCount()
    {
        int rowCount = 0;

        Cursor dbContent = query("SELECT count(*) FROM CARD");

        if (dbContent.moveToFirst())
        {
            do {
                rowCount = dbContent.getInt(0);

            } while (dbContent.moveToNext());
        }

        return rowCount;
    }

    public void saveCard( byte[] image,int occID, String message) throws SQLiteException
    {
        int messageID = addMessageEntry(message);

        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new  ContentValues();

        cv.put("name",   System.currentTimeMillis()+"");
        cv.put("cardImg",   image);
        cv.put("url",   " ");
        cv.put("occasionTemplateID", occID);
        cv.put("messageID", messageID);

        database.insert("Card",null,cv);
    }

    public int addMessageEntry(String message) throws SQLiteException
    {
        int rowCount = getCardCount();
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new  ContentValues();

        cv.put("text",   message);

        database.insert("Message",null,cv);

        return rowCount;
    }

}
