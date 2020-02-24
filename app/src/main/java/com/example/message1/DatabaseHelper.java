package com.example.message1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{

    // right now we are able to create only one table per database... I don't know why.
    // and in case if you are worried then case doesn't matter.

    public DatabaseHelper(Context context, String databse_name)
    {
        super(context, databse_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE table1 (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,PHONE TEXT,IMAGE TEXT, ABOUT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS table1");
    }

    public boolean insertData(String name, String phone, String image, String about)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("PHONE", phone);
        contentValues.put("IMAGE", image);
        contentValues.put("ABOUT", about);
        long res = db.insert("table1", null, contentValues);

        if(res == -1)
            return false;

        return true;
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from table1", null);
        return cursor;
    }

    public boolean updateData(String id, String name, String surname, String marks)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("NAME", name);
        contentValues.put("SURNAME", surname);
        contentValues.put("MARKS", marks);

        db.update("table1",contentValues, "id = ?", new String[] { id });

        return true;
    }

    public int deleteData(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("table1", "id = ?", new String[] { id });
    }
}

