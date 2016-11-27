package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocationDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FILENAME = "locations.db";

    private static final String CREATE_STATEMENT = "" +
            "create table locations(" +
            " locID integer primary key autoincrement," +
            " latitude real not null," +
            " longitude real not null)";

    private static final String DROP_DATABASE = "" +
            "drop table locations";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {sqLiteDatabase.execSQL(CREATE_STATEMENT);}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL(DROP_DATABASE);
        sqLiteDatabase.execSQL(CREATE_STATEMENT);
    }

    public LocationDBHelper(Context context){
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    public List<Location> getAllLocations() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Location> coordinateResults = new ArrayList<>();

        String[] columns = new String[] {"locID", "latitude", "longitude"};

        String where = "";
        String[] whereArgs = new String[] {};
        String groupBy = "";
        String groupArgs = "";
        String orderBy = "locID";

        Cursor cursor = sqLiteDatabase.query("locations", columns, where, whereArgs,
                groupBy, groupArgs, orderBy);

        cursor.moveToFirst();


        while(!cursor.isAfterLast()){
            int LocID = cursor.getInt(0);
            double latitude = cursor.getDouble(1);
            double longitude = cursor.getDouble(2);

            coordinateResults.add(new Location(LocID, latitude, longitude));
            cursor.moveToNext();
        }
        cursor.close();
        return coordinateResults;
    }

    public List<Integer> getAllLocationID() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Integer> IDList = new ArrayList<>();

        String[] columns = new String[] {"locID"};

        String where = "";
        String[] whereArgs = new String[] {};
        String groupBy = "";
        String groupArgs = "";
        String orderBy = "locID";

        Cursor cursor = sqLiteDatabase.query("locations", columns, where, whereArgs,
                groupBy, groupArgs, orderBy);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            int LocID = cursor.getInt(0);
            IDList.add(LocID);
            cursor.moveToNext();
        }
        cursor.close();

        return IDList;
    }

    public void deleteLocation(int LocID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("locations", "locID=?", new String[] {Integer.toString(LocID)});
    }

    public Location addNewLocation(double latitude, double longitude){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);

        int LocID = (int) sqLiteDatabase.insert("locations", null, contentValues);

        Location location = new Location(LocID, latitude, longitude);
        return location;
    }
}
