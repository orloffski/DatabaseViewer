package by.madcat.development.databaseviewer;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import by.madcat.development.databaseviewer.SQLiteData.DatabaseDescription.*;

public class QueryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int QUERIES_LOADER = 0;
    public static final String DATABASE_NAME = "database_name";
    public static final String URI_ID = "uri_id";
    public static final String QUERY_ACTION = "query_action";

    public static final int QUERY_ADD = 1;
    public static final int QUERY_EDIT = 2;

    private int action;
    private String databaseName;
    private Uri uri;
    private int id;

    private EditText queryText;
    private EditText queryName;
    private FrameLayout resultFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        action = getIntent().getIntExtra(QUERY_ACTION, 1);
        databaseName = getIntent().getStringExtra(DATABASE_NAME);
        id = getIntent().getIntExtra(URI_ID, 0);

        queryText = (EditText)findViewById(R.id.query_text);
        queryName = (EditText)findViewById(R.id.query_name);
        resultFrameLayout = (FrameLayout)findViewById(R.id.result_frame);

        setTitle("Query to database '" + databaseName + "'");

        if(id != 0) {
            uri = Query.buildQueriesUri(id);
            getLoaderManager().initLoader(QUERIES_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.query_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.query_save:
                String message = "";
                ContentValues contentValues = new ContentValues();
                contentValues.put(Query.QUERY_NAME, queryName.getText().toString());
                contentValues.put(Query.QUERY_TEXT, queryText.getText().toString());

                switch (action){
                    case QUERY_ADD:
                        Uri newQueryUri = this.getContentResolver().insert(Query.CONTENT_URI, contentValues);

                        if(newQueryUri != null) {
                            message = "Query saved";
                            action = QUERY_EDIT;
                            uri = newQueryUri;
                        }else
                            message = "Query saved";
                        break;
                    case QUERY_EDIT:
                        int updatedQueryRows = this.getContentResolver().update(uri, contentValues, null, null);

                        if(updatedQueryRows > 0)
                            message = "Query updated";
                        else
                            message = "Query not updated";
                        break;
                }

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getIntent(Context context, String databaseName, int id, int action){
        Intent intent = new Intent(context, QueryActivity.class);
        intent.putExtra(DATABASE_NAME, databaseName);
        intent.putExtra(URI_ID, id);
        intent.putExtra(QUERY_ACTION, action);
        return intent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case QUERIES_LOADER:
                return new CursorLoader(this,
                        uri,
                        null,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            int nameIndex = data.getColumnIndex(Query.QUERY_NAME);
            int textIndex = data.getColumnIndex(Query.QUERY_TEXT);

            queryName.setText(data.getString(nameIndex));
            queryText.setText(data.getString(textIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
