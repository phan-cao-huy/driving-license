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
    private static final int DATABASE_VERSION = 7; // Upgraded for critical questions logic

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
    private static final String COLUMN_Q_IMAGE_NAME = "image_name";
    private static final String COLUMN_Q_IS_CRITICAL = "is_critical";
    private static final String COLUMN_Q_LICENSE_CLASS = "license_class";
    
    // New columns for user progress
    private static final String COLUMN_Q_USER_ANSWER = "user_answer"; // 0: none, 1-4: chosen
    private static final String COLUMN_Q_IS_ANSWERED = "is_answered"; // 0: no, 1: yes

    // Table Traffic Signs
    private static final String TABLE_SIGNS = "traffic_signs";
    private static final String COLUMN_S_NAME = "name";
    private static final String COLUMN_S_DESCRIPTION = "description";
    private static final String COLUMN_S_IMAGE_NAME = "image_name";
    private static final String COLUMN_S_CATEGORY = "category";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuestionsTable = "CREATE TABLE " + TABLE_QUESTIONS + " (" +
                COLUMN_Q_ID + " INTEGER, " + // Removed AUTOINCREMENT to keep JSON ID
                COLUMN_Q_CONTENT + " TEXT, " +
                COLUMN_Q_OPTION_A + " TEXT, " +
                COLUMN_Q_OPTION_B + " TEXT, " +
                COLUMN_Q_OPTION_C + " TEXT, " +
                COLUMN_Q_OPTION_D + " TEXT, " +
                COLUMN_Q_CORRECT_ANSWER + " INTEGER, " +
                COLUMN_Q_EXPLANATION + " TEXT, " +
                COLUMN_Q_IMAGE_NAME + " TEXT, " +
                COLUMN_Q_IS_CRITICAL + " INTEGER, " +
                COLUMN_Q_LICENSE_CLASS + " TEXT, " +
                COLUMN_Q_USER_ANSWER + " INTEGER DEFAULT 0, " +
                COLUMN_Q_IS_ANSWERED + " INTEGER DEFAULT 0)";

        String createSignsTable = "CREATE TABLE " + TABLE_SIGNS + " (" +
                COLUMN_S_NAME + " TEXT, " +
                COLUMN_S_DESCRIPTION + " TEXT, " +
                COLUMN_S_IMAGE_NAME + " TEXT, " +
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
        values.put(COLUMN_Q_ID, question.getId());
        values.put(COLUMN_Q_CONTENT, question.getContent());
        values.put(COLUMN_Q_OPTION_A, question.getOptionA());
        values.put(COLUMN_Q_OPTION_B, question.getOptionB());
        values.put(COLUMN_Q_OPTION_C, question.getOptionC());
        values.put(COLUMN_Q_OPTION_D, question.getOptionD());
        values.put(COLUMN_Q_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(COLUMN_Q_EXPLANATION, question.getExplanation());
        values.put(COLUMN_Q_IMAGE_NAME, question.getImageName());
        values.put(COLUMN_Q_IS_CRITICAL, question.isCritical() ? 1 : 0);
        values.put(COLUMN_Q_LICENSE_CLASS, licenseClass);

        db.insert(TABLE_QUESTIONS, null, values);
    }

    public void updateUserAnswer(int questionId, int userAnswer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_Q_USER_ANSWER, userAnswer);
        values.put(COLUMN_Q_IS_ANSWERED, 1);
        db.update(TABLE_QUESTIONS, values, COLUMN_Q_ID + "=?", new String[]{String.valueOf(questionId)});
    }

    public List<Question> getFilteredQuestions(String licenseClass, String filterType) {
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_Q_LICENSE_CLASS + "=?";
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(licenseClass);

        switch (filterType) {
            case "DONE":
                selection += " AND " + COLUMN_Q_IS_ANSWERED + "=1";
                break;
            case "NOT_DONE":
                selection += " AND " + COLUMN_Q_IS_ANSWERED + "=0";
                break;
            case "WRONG":
                selection += " AND " + COLUMN_Q_IS_ANSWERED + "=1 AND " + COLUMN_Q_USER_ANSWER + "!=" + COLUMN_Q_CORRECT_ANSWER;
                break;
            case "CORRECT":
                selection += " AND " + COLUMN_Q_IS_ANSWERED + "=1 AND " + COLUMN_Q_USER_ANSWER + "==" + COLUMN_Q_CORRECT_ANSWER;
                break;
            case "HAS_IMAGE":
                selection += " AND (" + COLUMN_Q_IMAGE_NAME + " IS NOT NULL AND " + COLUMN_Q_IMAGE_NAME + " != '')";
                break;
            case "CRITICAL":
                selection += " AND " + COLUMN_Q_IS_CRITICAL + "=1";
                break;
            case "CONCEPTS":
                if (licenseClass.startsWith("A")) {
                    selection += " AND " + COLUMN_Q_ID + " BETWEEN 1 AND 110";
                } else {
                    selection += " AND " + COLUMN_Q_ID + " BETWEEN 1 AND 180";
                }
                break;
            default: // ALL
                break;
        }

        Cursor cursor = db.query(TABLE_QUESTIONS, null, selection,
                selectionArgs.toArray(new String[0]), null, null, null);

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
                        null,
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_Q_IMAGE_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Q_IS_CRITICAL)) == 1
                );
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    public List<Question> getQuestionsByClass(String licenseClass) {
        return getFilteredQuestions(licenseClass, "ALL");
    }

    public int getUserAnswer(int questionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, new String[]{COLUMN_Q_USER_ANSWER}, COLUMN_Q_ID + "=?",
                new String[]{String.valueOf(questionId)}, null, null, null);
        int answer = 0;
        if (cursor.moveToFirst()) {
            answer = cursor.getInt(0);
        }
        cursor.close();
        return answer;
    }

    public void addTrafficSign(TrafficSign sign) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_S_NAME, sign.getName());
        values.put(COLUMN_S_DESCRIPTION, sign.getDescription());
        values.put(COLUMN_S_IMAGE_NAME, sign.getImageName());
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
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_S_IMAGE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_S_CATEGORY))
                );
                signList.add(sign);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return signList;
    }
}
