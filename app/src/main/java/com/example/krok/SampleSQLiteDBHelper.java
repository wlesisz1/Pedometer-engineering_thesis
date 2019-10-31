package com.example.krok;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.LogPrinter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SampleSQLiteDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "KROKO_BAZA5.db";
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

    public static final String STEPS_TABLE_NAME = "steps";
    public static final String STEPS_ID = "_id";
    public static final String STEPS_DATE = "_date";
    public static final String STEPS_STARTAMOUNT = "_start";
    public static final String STEPS_ENDAMOUNT = "_end";

    public static final String TRIPSID_TABLE_NAME = "tripsid";
    public static final String TRIPID = "_id";
    public static final String TRIPNAME = "_name";

    private static final int DATABASE_VERSION = 121;

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

        sqLiteDatabase.execSQL("create table " + KROKI_TABLE_NAME + " (" + KROKI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KROKI_START + " INTEGER, "
                + KROKI_END + " INTEGER);");
        sqLiteDatabase.execSQL("create table " + STEPS_TABLE_NAME + " ("
                + STEPS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STEPS_DATE + " TEXT, "
                + STEPS_STARTAMOUNT + " INTEGER, "
                + STEPS_ENDAMOUNT + " INTEGER);");
        sqLiteDatabase.execSQL("create table " + TRIPSID_TABLE_NAME + " (" + TRIPID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TRIPNAME + " TEXT);");

    }


    public void CreateNewTrip(SQLiteDatabase sqLiteDatabase, String Name) {
        sqLiteDatabase.execSQL("create table " + TRIP_TABLE_NAME + Name + " ("
                + TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TRIP_X + " DOUBLE, "
                + TRIP_Y + " DOUBLE, "
                + TRIP_HEIGHT + " INTEGER, "
                + TRIP_STEP + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        Cursor c = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();
        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }
        for (String table : tables) {
            if (table.startsWith("sqlite_")) {
                continue;
            }
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            sqLiteDatabase.execSQL(dropQuery);
        }

        onCreate(sqLiteDatabase);
    }


    public void AddTripMeasurement(Context context, Float X, Float Y, String Name, int HEIGHT, int STEP) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(SampleSQLiteDBHelper.TRIP_X, X);
        values.put(SampleSQLiteDBHelper.TRIP_Y, Y);
        values.put(SampleSQLiteDBHelper.TRIP_HEIGHT, Integer.valueOf(HEIGHT));
        values.put(SampleSQLiteDBHelper.TRIP_STEP, Integer.valueOf(STEP));

            try {
                database.insertOrThrow(SampleSQLiteDBHelper.TRIP_TABLE_NAME + Name, null, values);
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    public void AddTripIDAndCreateTable(Context context, String Name) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SampleSQLiteDBHelper.TRIPNAME, Name);

        try {
            database.insertOrThrow(SampleSQLiteDBHelper.TRIPSID_TABLE_NAME, null, values);
            //Create Table for Name

            CreateNewTrip(database, Name);
            AddTripMeasurement(context, 0f , 0f, "Empty",0, 0);

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
        if (Integer.valueOf(Height) > 0)
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

    public void AddEndSTEP(Context context, String Steps) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SampleSQLiteDBHelper.KROKI_END, Integer.valueOf(Steps));
        values.put(SampleSQLiteDBHelper.KROKI_START, Integer.valueOf(Steps));

        try {
            //Toast.makeText(context, values.get(SampleSQLiteDBHelper.WYSOKO_HEIGHT).toString(), Toast.LENGTH_SHORT).show();
            database.insertOrThrow(SampleSQLiteDBHelper.STEPS_TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void AddStartSTEP(Context context, String Steps) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dateWithoutTime = cal.getTime();
        values.put(SampleSQLiteDBHelper.STEPS_DATE, dateWithoutTime.toString());
        values.put(SampleSQLiteDBHelper.STEPS_STARTAMOUNT, Integer.valueOf(Steps));
        values.put(SampleSQLiteDBHelper.STEPS_ENDAMOUNT, Integer.valueOf(Steps));
        try {
            Toast.makeText(context, dateWithoutTime.toString(), Toast.LENGTH_SHORT).show();

            database.insertOrThrow(SampleSQLiteDBHelper.STEPS_TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public Cursor GetPomiar(Context context) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(WYSOKO_TABLE_NAME, new String[]{WYSOKO_ID, WYSOKO_TIME, WYSOKO_HEIGHT}, null, null, null, null, null);
        return cursor;
    }



    public Cursor GetTripNames(Context context) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TRIPSID_TABLE_NAME, new String[]{TRIPNAME}, null, null, null, null, null);
        return cursor;
    }


    public Cursor GetTrip(Context context, String Name) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TRIP_TABLE_NAME+Name, new String[]{TRIP_X, TRIP_Y, TRIP_HEIGHT, TRIP_STEP}, null, null, null, null, null);
        return cursor;
    }

    public Cursor GetPomiarSteps(Context context) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(STEPS_TABLE_NAME, new String[]{STEPS_DATE, STEPS_ENDAMOUNT}, null, null, null, null, null);
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
        database.execSQL("drop table if exists " + STEPS_TABLE_NAME);
        onCreate(database);
    }

    public void ClearTrip(String ID) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("drop table if exists " + TRIP_TABLE_NAME + ID);
    }
}


