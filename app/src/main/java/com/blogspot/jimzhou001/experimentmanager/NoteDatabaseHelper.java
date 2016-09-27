package com.blogspot.jimzhou001.experimentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_NOTE = "create table Notes ("
            + "id integer primary key autoincrement,"
            + "title text,"
            + "date text,"
            + "time text,"
            + "millis integer)";

    public NoteDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exits Notes");
        onCreate(sqLiteDatabase);
    }

    public static void addNote(SQLiteDatabase sqLiteDatabase, String title, String date, String time, long millis) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("date", date);
        values.put("time", time);
        values.put("millis", millis);
        sqLiteDatabase.insert("Notes", null, values);
    }

}
