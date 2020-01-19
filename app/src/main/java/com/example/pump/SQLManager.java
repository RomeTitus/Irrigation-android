package com.example.pump;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLManager extends SQLiteOpenHelper {
    private static final String DatabaseName = "PumpController";
    private static final int DatabaseVersion = 9;

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
                        "  Name Varchar(255)," +
                        "  InternalPath Varchar(255)," +
                        "  InternalPort Varchar(255)," +
                        "  ExternalPath Varchar(255)," +
                        "  ExternalPort Varchar(255)," +
                        "  Mac Varchar(255)" +
                        ");";
                sqLiteDatabase.execSQL(pumpConnection);

                String pumpSelection = "create table IF NOT EXISTS pumpSelection(" +
                        "  ID INTEGER PRIMARY KEY autoincrement," +
                        "  pumpConnectionID Integer," +
                        "  CONSTRAINT FK_pumpSelection FOREIGN KEY (pumpConnectionID) REFERENCES pumpConnection(id)" +
                        ");";
                sqLiteDatabase.execSQL(pumpSelection);


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
                        "  Name Varchar(255)," +
                        "  InternalPath Varchar(255)," +
                        "  InternalPort Varchar(255)," +
                        "  ExternalPath Varchar(255)," +
                        "  ExternalPort Varchar(255)," +
                        "  Mac Varchar(255)" +
                        ");";
                sqLiteDatabase.execSQL(pumpConnection);

                String pumpSelection = "create table IF NOT EXISTS pumpSelection(" +
                        "  ID INTEGER PRIMARY KEY autoincrement," +
                        "  pumpConnectionID Integer," +
                        "  CONSTRAINT FK_pumpSelection FOREIGN KEY (pumpConnectionID) REFERENCES pumpConnection(id)" +
                        ");";
                sqLiteDatabase.execSQL(pumpSelection);

            }
        }catch (Exception e){
            String drop = "drop table if exists pumpConnection";
            sqLiteDatabase.execSQL(drop);
            String pumpConnection = "create table IF NOT EXISTS pumpConnection(" +
                    "  ID INTEGER PRIMARY KEY autoincrement," +
                    "  Name Varchar(255)," +
                    "  InternalPath Varchar(255)," +
                    "  InternalPort Varchar(255)," +
                    "  ExternalPath Varchar(255)," +
                    "  ExternalPort Varchar(255)," +
                    "  Mac Varchar(255)" +
                    ");";
            sqLiteDatabase.execSQL(pumpConnection);

            String pumpSelection = "create table IF NOT EXISTS pumpSelection(" +
                    "  ID INTEGER PRIMARY KEY autoincrement," +
                    "  pumpConnectionID Integer," +
                    "  CONSTRAINT FK_pumpSelection FOREIGN KEY (pumpConnectionID) REFERENCES pumpConnection(id)" +
                    ");";
            sqLiteDatabase.execSQL(pumpSelection);

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
                        "  Name Varchar(255)," +
                        "  InternalPath Varchar(255)," +
                        "  InternalPort Varchar(255)," +
                        "  ExternalPath Varchar(255)," +
                        "  ExternalPort Varchar(255)," +
                        "  Mac Varchar(255)" +
                        ");";
                sqLiteDatabase.execSQL(pumpConnection);

                String pumpSelection = "create table IF NOT EXISTS pumpSelection(" +
                        "  ID INTEGER PRIMARY KEY autoincrement," +
                        "  pumpConnectionID Integer," +
                        "  CONSTRAINT FK_pumpSelection FOREIGN KEY (pumpConnectionID) REFERENCES pumpConnection(id)" +
                        ");";
                sqLiteDatabase.execSQL(pumpSelection);


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
                        "  Name Varchar(255)," +
                        "  InternalPath Varchar(255)," +
                        "  InternalPort Varchar(255)," +
                        "  ExternalPath Varchar(255)," +
                        "  ExternalPort Varchar(255)," +
                        "  Mac Varchar(255)" +
                        ");";
                sqLiteDatabase.execSQL(pumpConnection);

                String pumpSelection = "create table IF NOT EXISTS pumpSelection(" +
                        "  ID INTEGER PRIMARY KEY autoincrement," +
                        "  pumpConnectionID Integer," +
                        "  CONSTRAINT FK_pumpSelection FOREIGN KEY (pumpConnectionID) REFERENCES pumpConnection(id)" +
                        ");";
                sqLiteDatabase.execSQL(pumpSelection);

            }
        }catch (Exception e){
            String drop = "drop table if exists pumpConnection";
            sqLiteDatabase.execSQL(drop);
            String pumpConnection = "create table IF NOT EXISTS pumpConnection(" +
                    "  ID INTEGER PRIMARY KEY autoincrement," +
                    "  Name Varchar(255)," +
                    "  InternalPath Varchar(255)," +
                    "  InternalPort Varchar(255)," +
                    "  ExternalPath Varchar(255)," +
                    "  ExternalPort Varchar(255)," +
                    "  Mac Varchar(255)" +
                    ");";
            sqLiteDatabase.execSQL(pumpConnection);

            String pumpSelection = "create table IF NOT EXISTS pumpSelection(" +
                    "  ID INTEGER PRIMARY KEY autoincrement," +
                    "  pumpConnectionID Integer," +
                    "  CONSTRAINT FK_pumpSelection FOREIGN KEY (pumpConnectionID) REFERENCES pumpConnection(id)" +
                    ");";
            sqLiteDatabase.execSQL(pumpSelection);

        }
    }

    public void updateInternalPath(String path, String Port) {

        int id = getSelectedIndex();


        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("InternalPath", path);
        contentValues.put("InternalPort", Port);

        sqLiteDatabase.update("pumpConnection",contentValues,"ID = '" + id +"';",null);


        //int id = getSelectedIndex();
       // SQLiteDatabase sqLiteDatabase = getWritableDatabase();
       // String sql = "Update pumpConnection SET InternalPath = '"+path+"', InternalPort = '"+Port+"' where id = " + id;
       // sqLiteDatabase.rawQuery(sql, null);

    }

    public void updateExternalPathWithMac(String path, String Port, String Mac) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ExternalPath", path);
        contentValues.put("ExternalPort", Port);

        sqLiteDatabase.update("pumpConnection",contentValues,"Mac = '" + Mac +"';",null);


    }

    public void updateExternalPath(String path, String Port) {
        int id = getSelectedIndex();


             SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ExternalPath", path);
        contentValues.put("ExternalPort", Port);

        sqLiteDatabase.update("pumpConnection",contentValues,"ID = '" + id +"';",null);



       // SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        //String sql = "Update pumpConnection SET ExternalPath = '"+path+"', ExternalPort = '"+Port+"' where ID = '" + id + "';";
        //sqLiteDatabase.rawQuery(sql, null);
    }

    public Cursor getPath() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "Select pumCon.InternalPath,pumCon.InternalPort, pumCon.ExternalPath, pumCon.ExternalPort from pumpConnection pumCon, pumpSelection pumSel where pumSel.pumpConnectionID = pumCon.id";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);
        return data;
    }

    public Cursor getControllerIDandNames(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "Select pumCon.Name, pumCon.id  from pumpConnection pumCon";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);
        return data;
    }

    public Cursor getControllerName(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "Select pumCon.Name from pumpConnection pumCon, pumpSelection pumSel where pumSel.pumpConnectionID = pumCon.id";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);
        return data;
    }

    public String getControllerNameByMac(String mac){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "Select Name from pumpConnection pumCon where Mac = '" +mac+ "';";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);
        if (data.getCount() > 0) {
            data.moveToNext();
            String Name = data.getString(0);
            return Name;
        }
        else{
        return "Unknown Controller";
        }
    }

    public Cursor getSelectedMac(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "Select pumCon.Mac from pumpConnection pumCon, pumpSelection pumSel where pumSel.pumpConnectionID = pumCon.id";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);
        return data;
    }

    public void setSelectedController(String id){
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete("pumpSelection", null, null);
        contentValues.put("pumpConnectionID", id);
        sqLiteDatabase.insert("pumpSelection", null, contentValues);
    }

    public int getSelectedIndex(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "Select pumCon.id from pumpConnection pumCon, pumpSelection pumSel where pumSel.pumpConnectionID = pumCon.id";
        Cursor data = sqLiteDatabase.rawQuery(sql, null);


        if (data.getCount() > 0) {
            data.moveToNext();
            String id = data.getString(0);
            return Integer.parseInt(id);
        }else{
            return -1;
        }
    }

    public void deleteSelectedController(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int id =  getSelectedIndex();
        sqLiteDatabase.delete("pumpSelection", null, null);
        sqLiteDatabase.delete("pumpConnection", "id = " + id, null);
    }


    public void addNewControllerExternalAndInternal(String InternalConnection, String InternalPort, String ExternalConnection, String ExternalPort, String mac, String Name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", Name);
        contentValues.put("InternalPath", InternalConnection);
        contentValues.put("InternalPort", InternalPort);
        contentValues.put("ExternalPath", ExternalConnection);
        contentValues.put("ExternalPort", ExternalPort);
        contentValues.put("Mac", mac);


        Long rowID = sqLiteDatabase.insert("pumpConnection", null, contentValues);
        sqLiteDatabase.delete("pumpSelection", null, null);

        contentValues.clear();
        contentValues.put("pumpConnectionID", rowID);
        sqLiteDatabase.insert("pumpSelection", null, contentValues);
    }

    public void addNewControllerInternal(String InternalConnection, String InternalPort, String mac, String Name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", Name);
        contentValues.put("InternalPath", InternalConnection);
        contentValues.put("InternalPort", InternalPort);
        contentValues.put("Mac", mac);

        Long rowID = sqLiteDatabase.insert("pumpConnection", null, contentValues);
        sqLiteDatabase.delete("pumpSelection", null, null);

        contentValues.clear();
        contentValues.put("pumpConnectionID", rowID);
        sqLiteDatabase.insert("pumpSelection", null, contentValues);
    }

    public void addNewControllerExternal(String ExternalConnection, String ExternalPort, String mac, String Name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", Name);
        contentValues.put("ExternalPath", ExternalConnection);
        contentValues.put("ExternalPort", ExternalPort);
        contentValues.put("Mac", mac);

        Long rowID = sqLiteDatabase.insert("pumpConnection", null, contentValues);
        sqLiteDatabase.delete("pumpSelection", null, null);

        contentValues.clear();
        contentValues.put("pumpConnectionID", rowID);
        sqLiteDatabase.insert("pumpSelection", null, contentValues);
    }
}