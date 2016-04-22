package com.ishmeetgrewal.zerodegrees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "zeroDegrees";

    // Table Names
    private static final String TABLE_USER = "user";
    private static final String TABLE_LOCATION = "location";
    private static final String TABLE_CLOTHES = "clothes";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // USER Table - column nmaes
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_TEMP = "temp";
    private static final String KEY_USER_HOME = "homeID";

    // LOCATION Table - column names
    private static final String KEY_LOCATION_NAME = "loc_name";
    private static final String KEY_LOCATION_LAT = "loc_lat";
    private static final String KEY_LOCATION_LON = "loc_lon";
    private static final String KEY_LOCATION_TEMP = "loc_temp";
    private static final String KEY_LOCATION_WIND = "loc_wind";
    private static final String KEY_LOCATION_PRECIP = "loc_precip";
    private static final String KEY_LOCATION_VISI = "loc_visi";

    // CLOTHES Table - column names
    private static final String KEY_CLOTH_OWN = "cloth_owned";
    private static final String KEY_CLOTH_RES = "cloth_resource";



    // Table Create Statements

    // User table create statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_NAME + " TEXT,"
            + KEY_USER_TEMP + " INTEGER,"
            + KEY_USER_HOME + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // Location table create statement
    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION
            + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_LOCATION_NAME + " TEXT,"
            + KEY_LOCATION_LAT + " TEXT,"
            + KEY_LOCATION_LON + " TEXT,"
            + KEY_LOCATION_TEMP + " INTEGER,"
            + KEY_LOCATION_WIND + " INTEGER,"
            + KEY_LOCATION_PRECIP + " INTEGER,"
            + KEY_LOCATION_VISI + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    private static final String CREATE_TABLE_CLOTHES = "CREATE TABLE " + TABLE_CLOTHES
            + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_CLOTH_RES + " INTEGER,"
            + KEY_CLOTH_OWN + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_LOCATION);
        db.execSQL(CREATE_TABLE_CLOTHES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLOTHES);

        // create new tables
        onCreate(db);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


    /* USER */

    public long createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_TEMP, user.getTemp());
        values.put(KEY_USER_HOME, user.getHome());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long user_id = db.insert(TABLE_USER, null, values);

        return user_id;
    }

    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USER;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        User td = new User(
                (c.getString(c.getColumnIndex(KEY_USER_NAME))),
                (c.getInt(c.getColumnIndex(KEY_USER_HOME))),
                (c.getInt(c.getColumnIndex(KEY_USER_TEMP)))
                );

        return td;
    }

    public boolean userExistsInDB(){
        boolean result = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + TABLE_USER;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        int count = c.getInt(0);
        if(count>0){
            result = true;
        }else{
            result  = false;
        }
        return result;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_TEMP, user.getTemp());
        values.put(KEY_USER_HOME, user.getHome());

        // updating row
        return db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }


    /* LOCATION */

    public boolean locationsExistInDB(){
        boolean result = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + TABLE_LOCATION;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        int count = c.getInt(0);
        if(count>0){
            result = true;
        }else{
            result  = false;
        }
        return result;
    }

    public ArrayList<Place> getAllPlaces() {
        ArrayList<Place> places = new ArrayList<Place>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATION;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Place td = new Place(
                        (c.getInt(c.getColumnIndex(KEY_ID))),
                        (c.getString(c.getColumnIndex(KEY_LOCATION_NAME))),
                        (Double.parseDouble(c.getString(c.getColumnIndex(KEY_LOCATION_LAT)))),
                        (Double.parseDouble(c.getString(c.getColumnIndex(KEY_LOCATION_LON))))
                );

                td.setTemp((c.getInt(c.getColumnIndex(KEY_LOCATION_TEMP))));
                td.setWindSpeed((c.getString(c.getColumnIndex(KEY_LOCATION_WIND))));
                td.setPrecipitation((c.getString(c.getColumnIndex(KEY_LOCATION_PRECIP))));
                td.setVisibility((c.getString(c.getColumnIndex(KEY_LOCATION_VISI))));

                places.add(td);
            } while (c.moveToNext());
        }

        return places;
    }


    public long createLocation(Place place) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_NAME, place.getName());
        values.put(KEY_LOCATION_LAT, Double.toString(place.getLat()));
        values.put(KEY_LOCATION_LON, Double.toString(place.getLon()));
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long loc_id = db.insert(TABLE_LOCATION, null, values);

        return loc_id;
    }

    public Place getLocation(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_LOCATION + " WHERE " + KEY_LOCATION_NAME + " = '" + name + "'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Place td = new Place(
                (c.getInt(c.getColumnIndex(KEY_ID))),
                (c.getString(c.getColumnIndex(KEY_LOCATION_NAME))),
                (Double.parseDouble(c.getString(c.getColumnIndex(KEY_LOCATION_LAT)))),
                (Double.parseDouble(c.getString(c.getColumnIndex(KEY_LOCATION_LON))))
        );

        td.setTemp((c.getInt(c.getColumnIndex(KEY_LOCATION_TEMP))));
        td.setWindSpeed((c.getString(c.getColumnIndex(KEY_LOCATION_WIND))));
        td.setPrecipitation((c.getString(c.getColumnIndex(KEY_LOCATION_PRECIP))));
        td.setVisibility((c.getString(c.getColumnIndex(KEY_LOCATION_VISI))));

        return td;
    }

    public int updateLocation(Place place) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_LOCATION_NAME, place.getName());
        values.put(KEY_LOCATION_TEMP, place.getTemp());
        values.put(KEY_LOCATION_WIND, place.getWindSpeed());
        values.put(KEY_LOCATION_PRECIP, place.getPrecipitation());
        values.put(KEY_LOCATION_VISI, place.getVisibility());

        // updating row
        return db.update(TABLE_LOCATION, values, KEY_ID + " = ?",
                new String[] { String.valueOf(place.getId()) });
    }


    /* CLOTHES */

    public long createCloth(int id) {

        Log.d(LOG, "creating cloth "+ Integer.toString(id));

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLOTH_OWN, 0);
        values.put(KEY_CLOTH_RES, id);
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long cloth_id = db.insert(TABLE_CLOTHES, null, values);

        return cloth_id;
    }

    public int updateCloth(int cloth_id, boolean owned) {

        Log.d(LOG, "updating cloth "+ Integer.toString(cloth_id));

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CLOTH_RES, cloth_id);
        if(owned){
            values.put(KEY_CLOTH_OWN, 1);
        }else{
            values.put(KEY_CLOTH_OWN, 0);
        }

        // updating row
        return db.update(TABLE_CLOTHES, values, KEY_CLOTH_RES + " = ?",
                new String[] { String.valueOf(cloth_id) });
    }

    public boolean clothIsOwned(int cloth_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CLOTHES + " WHERE " + KEY_CLOTH_RES + " = " + cloth_id + "";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        int ownership = c.getInt(c.getColumnIndex(KEY_CLOTH_OWN));

        if(ownership == 1){
            return true;
        }else{
            return false;
        }
    }

    public boolean clothesExistsInDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + TABLE_CLOTHES;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        int count = c.getInt(0);
        return (count > 0);
    }

}
