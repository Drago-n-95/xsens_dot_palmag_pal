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

    public static final String COLLUMN_DIP_DIR = "DIP_DIR";
    public static final String COLLUMN_DIP = "DIP";
    public static final String COLLUMN_STRAT = "STRAT";

    public static final String COLLUMN_DATE = "DATE";
    public static final String COLLUMN_TIME = "TIME";


    public DataBaseHelper(@Nullable Context context) {
        super(context, "tt7.db", null, 1);
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
        String col_dip_dir = COLLUMN_DIP_DIR + ",";
        String col_dip = COLLUMN_DIP + ",";
        String col_strat = COLLUMN_STRAT + ",";
        String col_date = COLLUMN_DATE + ",";
        String col_time = COLLUMN_TIME ;
        String createTableStatement = "CREATE TABLE " + CORE_TABLE + " (" + col_id + col_az +
                col_av + col_z + col_lat + col_lon + col_core + col_subcore + col_note +
                col_proj + col_sun + col_alt + col_dip_dir + col_dip +
                col_strat + col_date + col_time + " )";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(CoreInfoClass coreInfoClass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLLUMN_AZ, coreInfoClass.getNorth());
        cv.put(COLLUMN_AV, coreInfoClass.getEast());
        cv.put(COLLUMN_Z, coreInfoClass.getZdir());
        cv.put(COLLUMN_LAT, coreInfoClass.getLatitude());
        cv.put(COLLUMN_LON, coreInfoClass.getLongitude());
        cv.put(COLLUMN_CORE, coreInfoClass.getCore());
        cv.put(COLLUMN_SUBCORE, coreInfoClass.getSubcore());
        cv.put(COLLUMN_NOTE, coreInfoClass.getNote());
        cv.put(COLLUMN_PROJ, coreInfoClass.getProject());
        cv.put(COLLUMN_SUN, coreInfoClass.getSunReading());
        cv.put(COLLUMN_ALT, coreInfoClass.getAltitude());
        cv.put(COLLUMN_DIP_DIR, coreInfoClass.getDip_Dir());
        cv.put(COLLUMN_DIP, coreInfoClass.getDip());
        cv.put(COLLUMN_STRAT, coreInfoClass.getStrat());
        cv.put(COLLUMN_DATE, coreInfoClass.getDate());
        cv.put(COLLUMN_TIME, coreInfoClass.getTime());

        long insert = db.insert(CORE_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }

    }

    public boolean deleteOne(CoreInfoClass coreInfoClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + CORE_TABLE + " WHERE " + COLLUMN_ID + " = " + coreInfoClass.getId();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public void updateOne(CoreInfoClass coreInfoClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLLUMN_AZ, coreInfoClass.getNorth());
        values.put(COLLUMN_AV, coreInfoClass.getEast());
        values.put(COLLUMN_Z, coreInfoClass.getZdir());
        values.put(COLLUMN_LAT, coreInfoClass.getLatitude());
        values.put(COLLUMN_LON, coreInfoClass.getLongitude());
        values.put(COLLUMN_CORE, coreInfoClass.getCore());
        values.put(COLLUMN_SUBCORE, coreInfoClass.getSubcore());
        values.put(COLLUMN_NOTE, coreInfoClass.getNote());
        values.put(COLLUMN_PROJ, coreInfoClass.getProject());
        values.put(COLLUMN_SUN, coreInfoClass.getSunReading());
        values.put(COLLUMN_ALT, coreInfoClass.getAltitude());
        values.put(COLLUMN_DIP_DIR, coreInfoClass.getDip_Dir());
        values.put(COLLUMN_DIP, coreInfoClass.getDip());
        values.put(COLLUMN_STRAT, coreInfoClass.getStrat());
        values.put(COLLUMN_DATE, coreInfoClass.getDate());
        values.put(COLLUMN_TIME, coreInfoClass.getTime());

        db.update(CORE_TABLE, values, COLLUMN_ID + " = " + coreInfoClass.getId(), null);
        //
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
                String dipDir = cursor.getString(12);
                String Dip = cursor.getString(13);
                String Strat = cursor.getString(14);
                String Date = cursor.getString(15);
                String Time = cursor.getString(16);

                CoreInfoClass cores = new CoreInfoClass(coreID, northDat, eastDat,
                        zDat, latDat, lonDat, core, subcore, note, proj, sun_read, alt, dipDir, Dip, Strat, Date, Time);
                returnList.add(cores);
            }while(cursor.moveToNext());
        }
        else{

        }

        cursor.close();
        db.close();
        return returnList;
    }

    public String LastCoreEntry() {
       String querying = "SELECT * FROM CORE_TABLE ORDER BY id DESC LIMIT 1";
       SQLiteDatabase db = this.getReadableDatabase();
       Cursor cursor = db.rawQuery(querying, null);

       if (cursor.moveToFirst()) {
           String coreData = cursor.getString(6);
           return coreData;
       }
       else {
           return "1";
       }
    }

    public String LastProjectNameEntry() {
        String querying = "SELECT * FROM CORE_TABLE ORDER BY id DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(querying, null);

        if (cursor.moveToFirst()) {
            String projectName = cursor.getString(9);
            return projectName;
        }
        else {
            return "Project Name";
        }
    }

    public int getLastId() {
        String querying = "SELECT * FROM CORE_TABLE ORDER BY id DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(querying, null);

        if (cursor.moveToFirst()) {
            int lastId = cursor.getInt(0);
            return lastId;
        }
        else {
            return 1;
        }
    }

}
