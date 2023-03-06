package com.xsens.dot.android.example.views;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String CORE_TABLE = "CORE_TABLE";
    public static final String COLLUMN_ID = "ID";
    public static final String COLLUMN_PROJ = "PROJ";
    public static final String COLLUMN_CORE = "CORE";
    public static final String COLLUMN_SUBCORE = "SUBCORE";
    public static final String COLLUMN_NOTE = "NOTE";


    public static final String COLLUMN_AZ = "AZ";
    public static final String COLLUMN_AV = "AV";
    public static final String COLLUMN_Z = "Z";
    public static final String COLLUMN_LAT = "LAT";
    public static final String COLLUMN_LON = "LON";
    public static final String COLLUMN_ALT = "ALT";
    public static final String COLLUMN_SUN = "SUN";



    public DataBaseHelper(@Nullable Context context) {
        super(context, "tt3.db", null, 1);
    }

    //this is called the first time you try to access a database object
    @Override
    public void onCreate(SQLiteDatabase db) {
        String col_id = COLLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        String col_core = COLLUMN_CORE + ",";
        String col_proj = COLLUMN_PROJ + ",";
        String col_az = COLLUMN_AZ + " DOUBLE, ";
        String col_av = COLLUMN_AV + " DOUBLE, ";
        String col_z = COLLUMN_Z + " DOUBLE, ";
        String col_lat = COLLUMN_LAT + " DOUBLE,";
        String col_lon = COLLUMN_LON + " DOUBLE, ";
        String col_alt = COLLUMN_ALT + " DOUBLE";
        String col_sun = COLLUMN_SUN + ",";
        String col_subcore = COLLUMN_SUBCORE + ",";
        String col_note = COLLUMN_NOTE + ",";
        String createTableStatement = "CREATE TABLE " + CORE_TABLE + " (" + col_id + col_az +
                col_av + col_z + col_lat + col_lon + col_core + col_subcore + col_note +
                col_proj + col_sun + col_alt + " )";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(CoreInfoClass coreInfoClass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLLUMN_CORE, (coreInfoClass.getCore()));
        cv.put(COLLUMN_SUBCORE, coreInfoClass.getSubcore());
        cv.put(COLLUMN_NOTE, coreInfoClass.getNote());
        cv.put(COLLUMN_PROJ, coreInfoClass.getProject());
        cv.put(COLLUMN_AZ, coreInfoClass.getNorth());
        cv.put(COLLUMN_AV, coreInfoClass.getZdir());
        cv.put(COLLUMN_Z, coreInfoClass.getEast());
        cv.put(COLLUMN_LAT, coreInfoClass.getLatitude());
        cv.put(COLLUMN_LON, coreInfoClass.getLongitude());
        cv.put(COLLUMN_ALT, coreInfoClass.getAltitude());
        cv.put(COLLUMN_SUN, coreInfoClass.getSunReading());

        long insert = db.insert(CORE_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public List<CoreInfoClass> getEverything() {
        List<CoreInfoClass> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + CORE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()){
            do {
                int coreID = cursor.getInt(0);
                String northDat = cursor.getString(1);
                String eastDat = cursor.getString(2);
                String zDat = cursor.getString(3);
                String latDat = cursor.getString(4);
                String lonDat = cursor.getString(5);
                String core = cursor.getString(6);
                String subcore = cursor.getString(7);
                String note = cursor.getString(8);
                String proj = cursor.getString(9);
                String sun_read = cursor.getString(10);
                String alt = cursor.getString(11);

                CoreInfoClass cores = new CoreInfoClass(coreID, northDat, eastDat,
                        zDat, latDat, lonDat, core, subcore, note, proj, sun_read, alt, "", "", "");
                returnList.add(cores);
            }while(cursor.moveToNext());
        }
        else{

        }

        cursor.close();
        db.close();
        return returnList;
    }

}
