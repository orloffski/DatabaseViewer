package by.madcat.development.databaseviewer;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.SQLiteData.DatabaseDescription.*;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQLQueriesGenerator;

public class QueryActivity extends AbstractApplicationActivity implements LoaderManager.LoaderCallbacks<Cursor>, DataReceiver {

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
    private TextView dataTextView;
    private FrameLayout resultFrameLayout;
    private FloatingActionButton queryRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        action = getIntent().getIntExtra(QUERY_ACTION, 1);
        databaseName = getIntent().getStringExtra(DATABASE_NAME);
        id = getIntent().getIntExtra(URI_ID, 0);

        queryText = (EditText)findViewById(R.id.query_text);
        queryName = (EditText)findViewById(R.id.query_name);
        dataTextView = (TextView)findViewById(R.id.dataTextView);
        resultFrameLayout = (FrameLayout)findViewById(R.id.result_frame);

        setTitle("Query to database '" + databaseName + "'");
        queryRun = (FloatingActionButton)findViewById(R.id.query_run);
        queryRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataTextView.setText("");

                ConnectModel connectModel = ConnectModel.getInstance("", "", "");
                Intent intent = new Intent(QueryActivity.this, RequestService.class);
                intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
                intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
                intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
                intent.putExtra(RequestService.EXECUTE_MODEL, 2);

                connectModel.setUserRequestToServer(MSSQLQueriesGenerator.userQuery(databaseName, queryText.getText().toString()));

                startService(intent);
            }
        });

        if(id != 0) {
            uri = Query.buildQueriesUri(id);
            getLoaderManager().initLoader(QUERIES_LOADER, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);
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

    @Override
    public void sendErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendConnectConfirmation() {

    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {
        dataTextView.setText(jsonArrayData);
    }

    @Override
    public void sendQueryExecutedNoResult() {

    }
}
