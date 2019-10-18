package com.example.pump;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLManager extends SQLiteOpenHelper {
    private static final String DatabaseName = "PumpController";
    private static final int DatabaseVersion = 5;

    public SQLManager(Context context) {
        super(context, DatabaseName, null, DatabaseVersion);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //onCreate(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase = getWritableDatabase();
            String sql = "Select Path,Port from pumpConnection";
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            if(data.getCount()>0) {
                String drop = "drop table if exists pumpConnection";
                sqLiteDatabase.execSQL(drop);
                //btnAddSlave.setText("");
                data.moveToNext();
                String path = data.getString(0);
                String Port = data.getString(1);
                String pumpConnection = "create table IF NOT EXISTS pumpConnection(" +
                        "  ID INTEGER PRIMARY KEY autoincrement," +
                        "  InternalPath Varchar(255)," +
                        "  InternalPort Varchar(255)," +
                        "  ExternalPath Varchar(255)," +
                        "  ExternalPort Varchar(255)" +
                        ");";
                sqLiteDatabase.execSQL(pumpConnection);

                ContentValues contentValues = new ContentValues();
                contentValues.put("InternalPath", path);
                contentValues.put("InternalPort", Port);
                long result = sqLiteDatabase.update("pumpConnection", contentValues, "ID = 1", null);
                if (result == 0) {
                    sqLiteDatabase.insert("pumpConnection", null, contentValues);
                }



            }else{
                String drop = "drop table if exists pumpConnection";
                sqLiteDatabase.execSQL(drop);
                String pumpConnection = "create table IF NOT EXISTS pumpConnection(" +
                        "  ID INTEGER PRIMARY KEY autoincrement," +
                        "  InternalPath Varchar(255)," +
                        "  InternalPort Varchar(255)," +
                        "  ExternalPath Varchar(255)," +
                        "  ExternalPort Varchar(255)" +
                        ");";
                sqLiteDatabase.execSQL(pumpConnection);
            }
        }catch (Exception e){
            String drop = "drop table if exists pumpConnection";
            sqLiteDatabase.execSQL(drop);
            String pumpConnection = "create table IF NOT EXISTS pumpConnection(" +
                    "  ID INTEGER PRIMARY KEY autoincrement," +
                    "  InternalPath Varchar(255)," +
                    "  InternalPort Varchar(255)," +
                    "  ExternalPath Varchar(255)," +
                    "  ExternalPort Varchar(255)" +
                    ");";
            sqLiteDatabase.execSQL(pumpConnection);
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try{
            sqLiteDatabase = getWritableDatabase();
            String sql = "Select Path,Port from pumpConnection";
            Cursor data = sqLiteDatabase.rawQuery(sql, null);
            if(data.getCount()>0) {
                String drop = "drop table if exists pumpConnection";
                sqLiteDatabase.execSQL(drop);
                //btnAddSlave.setText("");
                data.moveToNext();
                String path = data.getString(0);
                String Port = data.getString(1);
                String pumpConnection = "create table IF NOT EXISTS pumpConnection(" +
                        "  ID INTEGER PRIMARY KEY autoincrement," +
                        "  InternalPath Varchar(255)," +
                        "  InternalPort Varchar(255)," +
                        "  ExternalPath Varchar(255)," +
                        "  ExternalPort Varchar(255)" +
                        ");";
                sqLiteDatabase.execSQL(pumpConnection);

                ContentValues contentValues = new ContentValues();
                contentValues.put("InternalPath", path);
                contentValues.put("InternalPort", Port);
                long result = sqLiteDatabase.update("pumpConnection", contentValues, "ID = 1", null);
                if (result == 0) {
                    sqLiteDatabase.insert("pumpConnection", null, contentValues);
                }



            }else{
                String drop = "drop table if exists pumpConnection";
                sqLiteDatabase.execSQL(drop);
                String pumpConnection = "create table IF NOT EXISTS pumpConnection(" +
                        "  ID INTEGER PRIMARY KEY autoincrement," +
                        "  InternalPath Varchar(255)," +
                        "  InternalPort Varchar(255)," +
                        "  ExternalPath Varchar(255)," +
                        "  ExternalPort Varchar(255)" +
                        ");";
                sqLiteDatabase.execSQL(pumpConnection);
            }
        }catch (Exception e){
            String drop = "drop table if exists pumpConnection";
            sqLiteDatabase.execSQL(drop);
            String pumpConnection = "create table IF NOT EXISTS pumpConnection(" +
                    "  ID INTEGER PRIMARY KEY autoincrement," +
                    "  InternalPath Varchar(255)," +
                    "  InternalPort Varchar(255)," +
                    "  ExternalPath Varchar(255)," +
                    "  ExternalPort Varchar(255)" +
                    ");";
            sqLiteDatabase.execSQL(pumpConnection);
        }
    }

    public void updateInternalPath(String path, String Port) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();


        ContentValues contentValues = new ContentValues();
        contentValues.put("InternalPath", path);
        contentValues.put("InternalPort", Port);


        long result = sqLiteDatabase.update("pumpConnection", contentValues, "ID = 1", null);
        if (result == 0) {
            sqLiteDatabase.insert("pumpConnection", null, contentValues);
        }

    }

    public void updateExternalPath(String path, String Port) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ExternalPath", path);
        contentValues.put("ExternalPort", Port);

        long result = sqLiteDatabase.update("pumpConnection", contentValues, "ID = 1", null);
        if (result == 0) {
            sqLiteDatabase.insert("pumpConnection", null, contentValues);
        }
    }

    public Cursor getPath() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "Select InternalPath,InternalPort, ExternalPath, ExternalPort from pumpConnection";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);
        return data;
    }
}