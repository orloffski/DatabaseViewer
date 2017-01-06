package by.madcat.development.databaseviewer.SQLiteData;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
    public static final String AUTHORITY = "by.madcat.development.databaseviewer.SQLiteData";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Query implements BaseColumns{
        public static final String TABLE_NAME = "queries";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String QUERY_NAME = "query_name";
        public static final String QUERY_TEXT = "query_text";

        public static Uri buildQueriesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
