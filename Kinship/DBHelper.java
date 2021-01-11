package com.example.hossain.relationship;

import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hossain on 6/21/2020.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Information";
    public static final String DATABASE_TABLE_NAME = "information";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String FATHER = "father";
    public static final String MOTHER = "mother";
    public static final String GENDER = "gender";
    public static final String SPOUSE = "spouse";
    public static final String FATHERID = "fatherID";
    public static final String MOTHERID = "motherID";
    public static final String SPOUSEID = "spouseID";
    public static final String MOBILENO = "mobileNo";
    public static final String EMAIL = "email";
    public static final String PRIORITY = "priority"; // 0 or 1

    public static final String BOTH_GENDER = "Both";
    public static final String MALE_GENDER = "Male";
    public static final String FEMALE_GENDER = "Female";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE " + DATABASE_TABLE_NAME +
                        "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " name TEXT , " +
                        " father TEXT , " +
                        " mother TEXT , " +
                        " gender TEXT , " +
                        " spouse TEXT , " +
                        " mobileNo TEXT , " +
                        " email TEXT , " +
                        " fatherID INTEGER , " +
                        " motherID INTEGER , " +
                        " spouseID INTEGER , " +
                        " priority INTEGER );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
        onCreate(db);
    }

    public long insertInfo (String name, String father, String mother, String gender,String spouse,
                               String mobileNo, String email, Integer fatherID, Integer motherID, Integer spouseID, int priority) {
                                //int fatherID, int motherID, int spouseID, int priority
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("father", father);
        contentValues.put("mother", mother);
        contentValues.put("gender", gender);
        contentValues.put("spouse", spouse);

        contentValues.put("mobileNo", mobileNo);
        contentValues.put("email", email);

        contentValues.put("fatherID", fatherID);
        contentValues.put("motherID", motherID);
        contentValues.put("spouseID", spouseID);

        contentValues.put("priority", priority);

        long val = db.insert(DATABASE_TABLE_NAME, null, contentValues);

        return val;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DATABASE_TABLE_NAME);
        return numRows;
    }
    //updateInfo (Integer id, String name, String phone, String email, String street,String place)
    public boolean updateInfo (Integer id, String column, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(column, value);
        db.update(DATABASE_TABLE_NAME, contentValues, "id = ?", new String[] {Integer.toString(id)});
        return true;
    }
    public boolean updateInfoID (Integer id, String column, Integer value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(column, value);
        db.update(DATABASE_TABLE_NAME, contentValues, "id = ?", new String[] {Integer.toString(id)});
        return true;
    }
    public boolean updateInfo (Integer id, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(gender.equals("Male")) {
            contentValues.put(FATHER, "");
            contentValues.put(FATHERID, 0);
            db.update(DATABASE_TABLE_NAME, contentValues, FATHERID+" = ?", new String[] {Integer.toString(id)});
        }
        else if(gender.equals("Female")) {
            contentValues.put(MOTHER, "");
            contentValues.put(MOTHERID, 0);
            db.update(DATABASE_TABLE_NAME, contentValues, MOTHERID+" = ?", new String[] {Integer.toString(id)});
        }

        contentValues = new ContentValues();
        contentValues.put(SPOUSE, "");
        contentValues.put(SPOUSEID, 0);
        db.update(DATABASE_TABLE_NAME, contentValues, SPOUSEID+" = ?", new String[] {Integer.toString(id)});

        return true;
    }
    public boolean updatePriority (Integer id, Integer value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PRIORITY, value);
        db.update(DATABASE_TABLE_NAME, contentValues, "id = ?", new String[] {Integer.toString(id)});
        return true;
    }

    public Integer deleteInfo (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DATABASE_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor getPriorityInfo() {
        //ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id, name FROM " + DATABASE_TABLE_NAME + " WHERE priority=" + 1 +" ORDER BY name";
        Cursor cursor =  db.rawQuery( query, null );

        return cursor;
    }
    public Cursor getAllInfo() {
        //ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DATABASE_TABLE_NAME + " ORDER BY name";
        Cursor cursor =  db.rawQuery( query, null );

        return cursor;
    }
    public Cursor getInfo(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE id=" + id;
        Cursor res =  db.rawQuery( query, null );
        return res;
    }
    public Cursor getQueryInfo(String name) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE name = ?";
        //Cursor res =  db.rawQuery( query, null );
        Cursor res = db.rawQuery(query, new String[]{name});
        //Cursor cursor = db.
        return res;
    }

    public Cursor getChildren(String name, String fOrM){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if(fOrM.equals("Father"))
            query = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE father = ?";
        else
            query = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE mother = ?";
        //Cursor res =  db.rawQuery( query, null );
        Cursor res = db.rawQuery(query, new String[]{ name });
        //Cursor cursor = db.
        return res;
    }

    public ArrayList<String> getAllName(String gender){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> name = new ArrayList<String>();
        Cursor cursor;

        String query = "SELECT name FROM " + DATABASE_TABLE_NAME + " WHERE gender=? ORDER BY name";
        if(gender.equals(BOTH_GENDER))
            query = "SELECT name FROM " + DATABASE_TABLE_NAME + " ORDER BY name";

        cursor = db.rawQuery(query, new String[]{gender});

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            name.add(cursor.getString(cursor.getColumnIndex(NAME)));
            cursor.moveToNext();
        }
        return name;
    }
    public ArrayList<Integer> getAllID(String gender){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> id = new ArrayList<Integer>();
        Cursor cursor;
        String query = "SELECT id FROM " + DATABASE_TABLE_NAME + " WHERE gender=? ORDER BY name";
        if(gender.equals(BOTH_GENDER))
            query = "SELECT id FROM " + DATABASE_TABLE_NAME + " ORDER BY name";

        cursor = db.rawQuery(query, new String[]{gender});

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            id.add(cursor.getInt(cursor.getColumnIndex(ID)));
            cursor.moveToNext();
        }
        return id;
    }

    // single attribute
    public String getName(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT name FROM " + DATABASE_TABLE_NAME + " WHERE id=" + id;
        Cursor res =  db.rawQuery( query, null );

        res.moveToFirst();

        return res.getString(res.getColumnIndex(NAME));
    }
    public String getGender(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT gender FROM " + DATABASE_TABLE_NAME + " WHERE id=" + id;
        Cursor res =  db.rawQuery( query, null );

        res.moveToFirst();

        return res.getString(res.getColumnIndex(GENDER));
    }
    public String getFather(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT father FROM " + DATABASE_TABLE_NAME + " WHERE id=" + id;
        Cursor res =  db.rawQuery( query, null );

        res.moveToFirst();

        return res.getString(res.getColumnIndex(FATHER));
    }
    public String getMother(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT mother FROM " + DATABASE_TABLE_NAME + " WHERE id=" + id;
        Cursor res =  db.rawQuery( query, null );

        res.moveToFirst();

        return res.getString(res.getColumnIndex(MOTHER));
    }
    public String getSpouse(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT spouse FROM " + DATABASE_TABLE_NAME + " WHERE id=" + id;
        Cursor res =  db.rawQuery( query, null );

        res.moveToFirst();

        return res.getString(res.getColumnIndex(SPOUSE));
    }
    public Integer getPriority(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+PRIORITY+" FROM " + DATABASE_TABLE_NAME + " WHERE id=" + id;
        Cursor res =  db.rawQuery( query, null );

        res.moveToFirst();

        return res.getInt(res.getColumnIndex(PRIORITY));
    }

    public void dropAndCreate(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
        onCreate(db);
    }
}