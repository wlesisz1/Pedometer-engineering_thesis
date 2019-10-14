package com.example.krok;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SampleSQLiteDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "KROKO_BAZA4.db";
    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_COLUMN_3 = "_id";
    public static final String SETTINGS_COLUMN_1 = "type";
    public static final String SETTINGS_COLUMN_2 = "number";
    public static final String WYSOKO_TABLE_NAME = "high";
    public static final String WYSOKO_ID = "_id";
    public static final String WYSOKO_HEIGHT = "height";
    public static final String WYSOKO_TIME = "time";
    public static final String KROKI_TABLE_NAME = "kroki";
    public static final String KROKI_ID = "_id";
    public static final String KROKI_START = "start";
    public static final String KROKI_END = "_end";
    public static final String TRIP_TABLE_NAME = "trips";
    public static final String TRIP_ID = "_id";
    public static final String TRIP_X = "_dimX";
    public static final String TRIP_Y = "_dimY";
    public static final String TRIP_STEP = "_step";
    public static final String TRIP_HEIGHT = "_height";


    private static final int DATABASE_VERSION = 100;

    public SampleSQLiteDBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public SampleSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + SETTINGS_TABLE_NAME + " (" + SETTINGS_COLUMN_3 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SETTINGS_COLUMN_1 + " TEXT, "
                + SETTINGS_COLUMN_2 + " INTEGER);");
        sqLiteDatabase.execSQL("create table " + WYSOKO_TABLE_NAME + " (" + WYSOKO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WYSOKO_TIME + " INTEGER, "
                + WYSOKO_HEIGHT + " INTEGER);");

        sqLiteDatabase.execSQL("create table " + KROKI_TABLE_NAME + " ("
                + KROKI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KROKI_START + " INTEGER, "
                + KROKI_END + " INTEGER);");


    }
    public void CreateNewTrip(SQLiteDatabase sqLiteDatabase, String ID)
    {
    sqLiteDatabase.execSQL("create table " + TRIP_TABLE_NAME + ID + " ("
            + TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TRIP_X + " INTEGER, "
            + TRIP_Y + " INTEGER, "
            + TRIP_HEIGHT + " INTEGER, "
            + TRIP_STEP + " INTEGER);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + SETTINGS_TABLE_NAME);

        sqLiteDatabase.execSQL("drop table if exists " + WYSOKO_TABLE_NAME);
        sqLiteDatabase.execSQL("drop table if exists " + KROKI_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public void AddPomiarTrip(Context context, String X, String Y, String ID, String HEIGHT, String STEP) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int tsLong = (int) (System.currentTimeMillis() / 1000);
        values.put(SampleSQLiteDBHelper.TRIP_ID, tsLong);
        values.put(SampleSQLiteDBHelper.TRIP_X, Integer.valueOf(X));
        values.put(SampleSQLiteDBHelper.TRIP_Y, Integer.valueOf(Y));
        values.put(SampleSQLiteDBHelper.TRIP_HEIGHT, Integer.valueOf(HEIGHT));
        values.put(SampleSQLiteDBHelper.TRIP_STEP, Integer.valueOf(STEP));
        if (Integer.valueOf(HEIGHT)>0)
            try {
                database.insertOrThrow(SampleSQLiteDBHelper.TRIP_TABLE_NAME+ID, null, values);
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }


    public void AddPomiar(Context context, String Height) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int tsLong = (int) (System.currentTimeMillis() / 1000);
        values.put(SampleSQLiteDBHelper.WYSOKO_TIME, tsLong);
        values.put(SampleSQLiteDBHelper.WYSOKO_HEIGHT, Integer.valueOf(Height));
        if (Integer.valueOf(Height)>0)
        try {
            database.insertOrThrow(SampleSQLiteDBHelper.WYSOKO_TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void AddSteps(Context context, String Steps) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SampleSQLiteDBHelper.KROKI_END, Integer.valueOf(Steps));
        values.put(SampleSQLiteDBHelper.KROKI_START, Integer.valueOf(Steps));
        try {
            //Toast.makeText(context, values.get(SampleSQLiteDBHelper.WYSOKO_HEIGHT).toString(), Toast.LENGTH_SHORT).show();
            database.insertOrThrow(SampleSQLiteDBHelper.KROKI_TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public Cursor GetPomiar(Context context) {
        SQLiteDatabase database = this.getReadableDatabase();

        //  Cursor res = database.rawQuery("SELECT * FROM " + SampleSQLiteDBHelper.WYSOKO_TABLE_NAME,null);
        Cursor cursor = database.query(WYSOKO_TABLE_NAME, new String[]{WYSOKO_ID, WYSOKO_TIME, WYSOKO_HEIGHT}, null, null, null, null, null);
        //  Toast.makeText(context, res., Toast.LENGTH_SHORT).show();

        //  database.close();
        return cursor;

    }

    public void Close() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.close();
    }

    public void Clear() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("drop table if exists " + SETTINGS_TABLE_NAME);

        database.execSQL("drop table if exists " + WYSOKO_TABLE_NAME);
        database.execSQL("drop table if exists " + KROKI_TABLE_NAME);
        onCreate(database);
    }
}


