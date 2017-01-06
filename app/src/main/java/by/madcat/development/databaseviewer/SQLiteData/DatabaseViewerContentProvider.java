package by.madcat.development.databaseviewer.SQLiteData;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.SQLiteData.DatabaseDescription.*;

public class DatabaseViewerContentProvider extends ContentProvider {
    private DatabaseViewerDatabaseHelper dbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ONE_QUERY = 1;
    private static final int QUERIES = 2;

    static {
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, Query.TABLE_NAME + "/#", ONE_QUERY);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, Query.TABLE_NAME, QUERIES);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseViewerDatabaseHelper(getContext());
        return true;
    }

    public DatabaseViewerContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;
        String id;

        switch (uriMatcher.match(uri)){
            case ONE_QUERY:
                id = uri.getLastPathSegment();
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(Query.TABLE_NAME, Query._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        return numberOfRowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newUri = null;
        long rowId;

        switch (uriMatcher.match(uri)){
            case QUERIES:
                rowId = dbHelper.getWritableDatabase().insert(Query.TABLE_NAME, null, values);
                if(rowId > 0){
                    newUri = Query.buildQueriesUri(rowId);
                    getContext().getContentResolver().notifyChange(uri, null);
                }else
                    throw new SQLException(getContext().getString(R.string.insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        return newUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case ONE_QUERY:
                queryBuilder.setTables(Query.TABLE_NAME);
                queryBuilder.appendWhere(Query._ID + "=" + uri.getLastPathSegment());
                break;
            case QUERIES:
                queryBuilder.setTables(Query.TABLE_NAME);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_query_uri) + uri);
        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int numberOfRowsUpdated;
        String id;

        switch (uriMatcher.match(uri)){
            case ONE_QUERY:
                id = uri.getLastPathSegment();
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(Query.TABLE_NAME, values, Query._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_update_uri) + uri);
        }

        if(numberOfRowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return numberOfRowsUpdated;
    }
}
