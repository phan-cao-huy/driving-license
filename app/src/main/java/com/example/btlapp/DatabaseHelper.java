package com.example.btlapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "driving_license.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // Table Questions
    private static final String TABLE_QUESTIONS = "questions";
    private static final String COLUMN_Q_ID = "id";
    private static final String COLUMN_Q_CONTENT = "content";
    private static final String COLUMN_Q_OPTION_A = "option_a";
    private static final String COLUMN_Q_OPTION_B = "option_b";
    private static final String COLUMN_Q_OPTION_C = "option_c";
    private static final String COLUMN_Q_OPTION_D = "option_d";
    private static final String COLUMN_Q_CORRECT_ANSWER = "correct_answer";
    private static final String COLUMN_Q_EXPLANATION = "explanation";
    private static final String COLUMN_Q_IMAGE_RES_ID = "image_res_id";
    private static final String COLUMN_Q_IS_CRITICAL = "is_critical";
    private static final String COLUMN_Q_LICENSE_CLASS = "license_class"; // A1, B1, etc.

    // Table Traffic Signs
    private static final String TABLE_SIGNS = "traffic_signs";
    private static final String COLUMN_S_NAME = "name";
    private static final String COLUMN_S_DESCRIPTION = "description";
    private static final String COLUMN_S_IMAGE_RES_ID = "image_res_id";
    private static final String COLUMN_S_CATEGORY = "category";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuestionsTable = "CREATE TABLE " + TABLE_QUESTIONS + " (" +
                COLUMN_Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_Q_CONTENT + " TEXT, " +
                COLUMN_Q_OPTION_A + " TEXT, " +
                COLUMN_Q_OPTION_B + " TEXT, " +
                COLUMN_Q_OPTION_C + " TEXT, " +
                COLUMN_Q_OPTION_D + " TEXT, " +
                COLUMN_Q_CORRECT_ANSWER + " INTEGER, " +
                COLUMN_Q_EXPLANATION + " TEXT, " +
                COLUMN_Q_IMAGE_RES_ID + " INTEGER, " +
                COLUMN_Q_IS_CRITICAL + " INTEGER, " +
                COLUMN_Q_LICENSE_CLASS + " TEXT)";

        String createSignsTable = "CREATE TABLE " + TABLE_SIGNS + " (" +
                COLUMN_S_NAME + " TEXT, " +
                COLUMN_S_DESCRIPTION + " TEXT, " +
                COLUMN_S_IMAGE_RES_ID + " INTEGER, " +
                COLUMN_S_CATEGORY + " TEXT)";

        db.execSQL(createQuestionsTable);
        db.execSQL(createSignsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGNS);
        onCreate(db);
    }

    public void addQuestion(Question question, String licenseClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // values.put(COLUMN_Q_ID, question.getId()); // Let DB handle ID or keep it fixed
        values.put(COLUMN_Q_CONTENT, question.getContent());
        values.put(COLUMN_Q_OPTION_A, question.getOptionA());
        values.put(COLUMN_Q_OPTION_B, question.getOptionB());
        values.put(COLUMN_Q_OPTION_C, question.getOptionC());
        values.put(COLUMN_Q_OPTION_D, question.getOptionD());
        values.put(COLUMN_Q_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(COLUMN_Q_EXPLANATION, question.getExplanation());
        values.put(COLUMN_Q_IMAGE_RES_ID, question.getImageResId());
        values.put(COLUMN_Q_IS_CRITICAL, question.isCritical() ? 1 : 0);
        values.put(COLUMN_Q_LICENSE_CLASS, licenseClass);

        db.insert(TABLE_QUESTIONS, null, values);
    }

    public List<Question> getQuestionsByClass(String licenseClass) {
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Search by class (e.g., "B1")
        Cursor cursor = db.query(TABLE_QUESTIONS, null, COLUMN_Q_LICENSE_CLASS + "=?",
                new String[]{licenseClass}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Q_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_OPTION_A)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_OPTION_B)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_OPTION_C)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_OPTION_D)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Q_CORRECT_ANSWER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_EXPLANATION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Q_IMAGE_RES_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Q_IS_CRITICAL)) == 1
                );
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    public void addTrafficSign(TrafficSign sign) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_S_NAME, sign.getName());
        values.put(COLUMN_S_DESCRIPTION, sign.getDescription());
        values.put(COLUMN_S_IMAGE_RES_ID, sign.getImageResId());
        values.put(COLUMN_S_CATEGORY, sign.getCategory());

        db.insert(TABLE_SIGNS, null, values);
    }

    public List<TrafficSign> getAllTrafficSigns() {
        List<TrafficSign> signList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SIGNS, null);

        if (cursor.moveToFirst()) {
            do {
                TrafficSign sign = new TrafficSign(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_S_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_S_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_S_IMAGE_RES_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_S_CATEGORY))
                );
                signList.add(sign);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return signList;
    }
}
