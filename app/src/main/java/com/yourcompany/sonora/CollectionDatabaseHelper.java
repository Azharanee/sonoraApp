package com.yourcompany.sonora;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class CollectionDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "collections.db";
    private static final int DATABASE_VERSION = 1;

    public CollectionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE collections (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, image_type TEXT)");
        db.execSQL("CREATE TABLE collection_music (id INTEGER PRIMARY KEY AUTOINCREMENT, collection_id INTEGER, music_path TEXT, FOREIGN KEY(collection_id) REFERENCES collections(id) ON DELETE CASCADE)"); // Added ON DELETE CASCADE
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // A simple approach for upgrade is to drop and recreate tables.
        // For production apps, consider ALTER TABLE or data migration.
        db.execSQL("DROP TABLE IF EXISTS collections");
        db.execSQL("DROP TABLE IF EXISTS collection_music");
        onCreate(db);
    }

    // Add a new collection
    public long addCollection(String name, String imageType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("image_type", imageType);
        // It's good practice to close the database when done with it
        long result = db.insert("collections", null, values);
        db.close(); // Close the database
        return result;
    }

    // Get all collections
    public Cursor getAllCollections() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Note: You would typically close the database after reading in the calling code
        return db.rawQuery("SELECT * FROM collections", null);
    }

    // Add music to a collection
    // <--- CORRECTED RETURN TYPE FROM boolean TO long --->
    public long addMusicToCollection(int collectionId, String musicPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("collection_id", collectionId);
        values.put("music_path", musicPath);
        // It's good practice to close the database when done with it
        long result = db.insert("collection_music", null, values);
        db.close(); // Close the database
        return result; // <--- Return the long result from insert --->
    }

    // Get music for a collection
    public Cursor getMusicForCollection(int collectionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Note: You would typically close the database after reading in the calling code
        return db.rawQuery("SELECT * FROM collection_music WHERE collection_id = ?", new String[]{String.valueOf(collectionId)});
    }

    // Remember to close the database cursor after using it in the calling code (e.g., in the Fragment/Activity)
    // And close the database itself when the Fragment/Activity is destroyed (e.g., in onDestroy())
}