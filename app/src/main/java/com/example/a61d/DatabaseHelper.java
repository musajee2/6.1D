package com.example.a61d;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Name
    private static final String DATABASE_NAME = "iTubeDB";

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PLAYLIST = "playlist";

    // Common column names
    private static final String KEY_ID = "id";

    // Users Table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    // Playlist Table - column names
    private static final String KEY_YOUTUBE_URL = "youtube_url";

    // Table Create Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_USERNAME + " TEXT," +
            KEY_EMAIL + " TEXT UNIQUE," +
            KEY_PASSWORD + " TEXT" +
            ")";

    private static final String CREATE_TABLE_PLAYLIST = "CREATE TABLE " + TABLE_PLAYLIST +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_YOUTUBE_URL + " TEXT" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PLAYLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);

        // create new tables
        onCreate(db);
    }

    // Adding new user
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());

        // insert row
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Method to check if a user with the given email exists in the database
    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                KEY_ID
        };
        String selection = KEY_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                TABLE_USERS,     // The table to query
                columns,         // The array of columns to return (null to return all)
                selection,       // The columns for the WHERE clause
                selectionArgs,   // The values for the WHERE clause
                null,            // don't group the rows
                null,            // don't filter by row groups
                null             // The sort order
        );

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    // Method to add YouTube URL to playlist
    public void addToPlaylist(String youtubeUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_YOUTUBE_URL, youtubeUrl);
        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }
    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {
                KEY_ID,
                KEY_USERNAME,
                KEY_EMAIL,
                KEY_PASSWORD
        };

        String selection = KEY_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                TABLE_USERS,             // The table to query
                columns,                 // The array of columns to return (null to return all)
                selection,               // The columns for the WHERE clause
                selectionArgs,           // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        if (cursor.moveToFirst()) {
            // Retrieve user data from the cursor
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String username = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
            String userEmail = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
            String password = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));

            // Create a new User object
            user = new User(username, userEmail, password);
            user.setId(id);
        }

        cursor.close();
        db.close();

        return user;
    }

    public ArrayList<String> getPlaylist() {
        ArrayList<String> playlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {KEY_YOUTUBE_URL};

        Cursor cursor = db.query(
                TABLE_PLAYLIST,     // The table to query
                columns,            // The array of columns to return (null to return all)
                null,               // The columns for the WHERE clause
                null,               // The values for the WHERE clause
                null,               // don't group the rows
                null,               // don't filter by row groups
                null                // The sort order
        );

        // Loop through all rows and add them to the playlist ArrayList
        if (cursor.moveToFirst()) {
            do {
                String youtubeUrl = cursor.getString(cursor.getColumnIndex(KEY_YOUTUBE_URL));
                playlist.add(youtubeUrl);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return playlist;
    }

    // More methods can be added for database operations as needed
}
