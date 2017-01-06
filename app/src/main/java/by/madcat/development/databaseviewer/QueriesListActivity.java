package by.madcat.development.databaseviewer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import by.madcat.development.databaseviewer.Adapters.QueriesListRecyclerViewAdapter;
import by.madcat.development.databaseviewer.SQLiteData.DatabaseDescription.*;

public class QueriesListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, QueriesListRecyclerViewAdapter.ClickListener {

    private static final int QUERIES_LOADER = 0;
    public static final String DATABASE_NAME = "database_name";

    private RecyclerView queriesList;
    private QueriesListRecyclerViewAdapter adapter;
    private String databaseName;

    private FloatingActionButton queryAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queries_list);

        this.databaseName = getIntent().getStringExtra(DATABASE_NAME);
        setTitle("Queries list on database '" + databaseName + "'");

        queriesList = (RecyclerView) findViewById(R.id.queriesList);
        queriesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        queriesList.setHasFixedSize(true);

        queryAdd = (FloatingActionButton)findViewById(R.id.query_add);
        queryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = QueryActivity.getIntent(getApplicationContext(), databaseName, 0, QueryActivity.QUERY_ADD);
                startActivity(intent);
            }
        });

        adapter = new QueriesListRecyclerViewAdapter(new QueriesListRecyclerViewAdapter.ClickListener(){
            @Override
            public void onClick(Uri queryUri) {
                Intent intent = QueryActivity.getIntent(getApplicationContext(), databaseName,
                        Integer.parseInt(queryUri.getLastPathSegment()), QueryActivity.QUERY_EDIT);
                startActivity(intent);
            }
        }, this);

        queriesList.setAdapter(adapter);

        getLoaderManager().initLoader(QUERIES_LOADER, null, this);
    }

    public static Intent getIntent(Context context, String databaseName){
        Intent intent = new Intent(context, QueriesListActivity.class);
        intent.putExtra(DATABASE_NAME, databaseName);
        return intent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case QUERIES_LOADER:
                return new CursorLoader(this,
                        Query.CONTENT_URI,
                        null,
                        null,
                        null,
                        Query.QUERY_NAME + " COLLATE NOCASE DESC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onClick(Uri queryUri) {

    }
}
