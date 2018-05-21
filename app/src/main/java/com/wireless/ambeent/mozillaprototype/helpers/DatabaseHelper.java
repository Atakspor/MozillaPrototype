package com.wireless.ambeent.mozillaprototype.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Atakan on 13.07.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper dbInstance;

    // Database Version. Increment this manually when database schema changes.
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "MozillaProMessenger";

    // Table Names
    public static final String TABLE_MESSAGES = "messageTable";


    // Messages Table - column names
    public static final String KEY_MESSAGE_ID = "messageId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_RECEIVER = "receiver";


    //Messages creating query.
    public static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " (" +
            KEY_MESSAGE_ID + " TEXT, "+
            KEY_MESSAGE + " TEXT, "+
            KEY_SENDER + " TEXT, " +
            KEY_RECEIVER +" TEXT);";



    //Making SQLite singleton.
    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    //Private constructor
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);

    }




    //Called everytime the app is launched. Deletes the messages that are older than 10 minutes
   /*
    public static void databaseMaintenance(Context context){
        long tenMinutesAgo = getCurrentTimeMilis() - (1000 * 60 *//* *10*//*);// 1000 ms = 1 sec, 60 sec = 1 min, total of 10 mins

        String table = TABLE_CHATMESSAGES;
        String comparisonString = KEY_TIMESTAMP + "<= " + tenMinutesAgo;

        int rowsDeleted = getInstance(context)
                .getWritableDatabase().delete(table, comparisonString, null);

        Log.i(TAG, "sqliteMaintanance: rowsDeleted: " + rowsDeleted);
    }*/



}
