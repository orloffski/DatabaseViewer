package by.madcat.development.databaseviewer.SQLiteData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import by.madcat.development.databaseviewer.SQLiteData.DatabaseDescription.*;

public class DatabaseViewerDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DatabaseViewer.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseViewerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_QUERIES_TABLE =
                "CREATE TABLE " + Query.TABLE_NAME + "(" +
                        Query._ID + " integer primary key, " +
                        Query.QUERY_NAME + " TEXT, " +
                        Query.QUERY_TEXT + " TEXT);";

        sqLiteDatabase.execSQL(CREATE_QUERIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
