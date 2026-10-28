package com.example.midexam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "student.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "students";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_STUDENT_ID = "student_id";
    private static final String COL_ENROLLED = "enrolled";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_STUDENT_ID + " TEXT NOT NULL UNIQUE, "
                + COL_ENROLLED + " INTEGER NOT NULL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    private ContentValues toValues(String name, String studentId, boolean enrolled) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_ENROLLED, enrolled ? 1 : 0);
        return values;
    }

    // ADD
    public boolean addStudent(String name, String studentId, boolean enrolled) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            return db.insertOrThrow(TABLE, null, toValues(name, studentId, enrolled)) != -1;
        } catch (Exception e) {
            return false; // biasanya karena Student ID sudah dipakai (UNIQUE)
        }
    }

    // VIEW
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_ID + ", " + COL_NAME + ", "
                + COL_STUDENT_ID + ", " + COL_ENROLLED + " FROM " + TABLE
                + " ORDER BY " + COL_NAME + " COLLATE NOCASE", null);
        try {
            while (c.moveToNext()) {
                list.add(new Student(
                        c.getLong(0),
                        c.getString(1),
                        c.getString(2),
                        c.getInt(3) == 1));
            }
        } finally {
            c.close();
        }
        return list;
    }

    // EDIT
    public boolean updateStudent(long id, String name, String studentId, boolean enrolled) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            return db.update(TABLE, toValues(name, studentId, enrolled),
                    COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // DELETE
    public boolean deleteStudent(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
}