package by.madcat.development.databaseviewer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.Adapters.TablesListRecyclerViewAdapter;
import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.ConnectData.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesList;

public class TablesListActivity extends AbstractApplicationActivity implements DataReceiver {

    public static final String DATABASE_NAME = "database_name";

    private String databaseName;

    private RecyclerView tablesList;
    private TablesListRecyclerViewAdapter adapter;
    private ArrayList<String> tables;

    private FloatingActionButton tableAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables_list);

        this.databaseName = getIntent().getStringExtra(DATABASE_NAME);

        tablesList = (RecyclerView)findViewById(R.id.tables_list);
        tablesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tablesList.setHasFixedSize(true);

        tables = new ArrayList<>();
        adapter = new TablesListRecyclerViewAdapter(tables, this, databaseName);

        tablesList.setAdapter(adapter);

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);

        tableAdd = (FloatingActionButton)findViewById(R.id.table_add);
        tableAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddEditTableActivity.getIntent(
                        getApplicationContext(),
                        AddEditTableActivity.TABLE_ADD,
                        "",
                        databaseName);
                startActivity(intent);
            }
        });

        loadTablesList(this.databaseName);

        setTitle("Tables list of '" + this.databaseName + "' database");
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);
    }

    public static Intent getIntent(Context context, String databaseName){
        Intent intent = new Intent(context, TablesListActivity.class);
        intent.putExtra(DATABASE_NAME, databaseName);
        return intent;
    }

    private void loadTablesList(String databaseName){
        ConnectModel model = ConnectModel.getInstance("", "", "");
        model.setUserRequestToServer(String.format(QueriesList.TABLES_LIST_QUERY, databaseName));

        Intent intent = new Intent(TablesListActivity.this, RequestService.class);
        intent.putExtra(RequestService.EXECUTE_MODEL, 2);
        startService(intent);
    }

    @Override
    public void sendErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendConnectConfirmation() {

    }

    @Override
    public void sendQueryExecutedNoResult() {
        loadTablesList(this.databaseName);
    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {
        try {
            tables.clear();
            JSONArray jsonArray = new JSONArray(jsonArrayData);
            for(int i = 0; i < jsonArray.length(); i++)
                tables.add(jsonArray.getJSONObject(i).getString("Name").toString());

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            // for Google Analytics
        }
    }
}
