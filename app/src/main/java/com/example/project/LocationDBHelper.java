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
            " name text primary key," +
            " strNum integer not null," +
            " strName text not null," +
            " city text not null," +
            " province text not null)";

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

        String[] columns = new String[] {"name", "strNum", "strName",  "city", "province"};

        String where = "";
        String[] whereArgs = new String[] {};
        String groupBy = "";
        String groupArgs = "";
        String orderBy = "name";

        Cursor cursor = sqLiteDatabase.query("locations", columns, where, whereArgs,
                groupBy, groupArgs, orderBy);

        cursor.moveToFirst();


        while(!cursor.isAfterLast()){
            String nameOfLocation = cursor.getString(0);
            int streetNumber = cursor.getInt(1);
            String streetName = cursor.getString(2);
            String city = cursor.getString(3);
            String province = cursor.getString(4);

            coordinateResults.add(new Location(nameOfLocation, streetNumber, streetName, city, province));
            cursor.moveToNext();
        }
        cursor.close();
        return coordinateResults;
    }

    public List<String> getAllLocationNames() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> IDList = new ArrayList<>();

        String[] columns = new String[] {"name"};

        String where = "";
        String[] whereArgs = new String[] {};
        String groupBy = "";
        String groupArgs = "";
        String orderBy = "name";

        Cursor cursor = sqLiteDatabase.query("locations", columns, where, whereArgs,
                groupBy, groupArgs, orderBy);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            String LocName = cursor.getString(0);
            IDList.add(LocName);
            cursor.moveToNext();
        }
        cursor.close();

        return IDList;
    }

    public void deleteLocation(String name){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("locations", "name=?", new String[] {name});
    }

    public Location addNewLocation(String nameOfLocation, int streetNumber, String streetName, String city,
                                                                                String province){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", nameOfLocation);
        contentValues.put("strNum", streetNumber);
        contentValues.put("strName", streetName);
        contentValues.put("city", city);
        contentValues.put("province", province);

        sqLiteDatabase.insert("locations", null, contentValues);

        Location location = new Location(nameOfLocation, streetNumber, streetName, city, province);
        return location;
    }
}
