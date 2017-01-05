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
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Models.PrimaryKeysModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQLQueriesGenerator;

public class TablesListActivity extends AbstractApplicationActivity implements DataReceiver {

    public static final String DATABASE_NAME = "database_name";

    private PrimaryKeysModel primaryKeysModel;

    private String databaseName;
    private boolean loadData;

    private RecyclerView tablesList;
    private TablesListRecyclerViewAdapter adapter;
    private ArrayList<String> tables;

    private FloatingActionButton tableAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables_list);

        this.loadData = false;
        this.databaseName = getIntent().getStringExtra(DATABASE_NAME);

        tablesList = (RecyclerView)findViewById(R.id.tables_list);
        tablesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tablesList.setHasFixedSize(true);

        tables = new ArrayList<>();
        adapter = new TablesListRecyclerViewAdapter(tables, this, databaseName);

        tablesList.setAdapter(adapter);

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

        loadPrimaryKeysList(this.databaseName);

        setTitle("Tables list of '" + this.databaseName + "' database");
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);

        if(this.loadData)
            loadTablesList(this.databaseName);
    }

    public static Intent getIntent(Context context, String databaseName){
        Intent intent = new Intent(context, TablesListActivity.class);
        intent.putExtra(DATABASE_NAME, databaseName);
        return intent;
    }

    private void loadPrimaryKeysList(String databaseName){
        ConnectModel model = ConnectModel.getInstance("", "", "");
        model.setUserRequestToServer(MSSQLQueriesGenerator.getPrimaryKeysList(databaseName));

        Intent intent = new Intent(TablesListActivity.this, RequestService.class);
        intent.putExtra(RequestService.EXECUTE_MODEL, 2);
        startService(intent);
    }

    private void loadTablesList(String databaseName){
        ConnectModel model = ConnectModel.getInstance("", "", "");
        model.setUserRequestToServer(MSSQLQueriesGenerator.getTablesList(databaseName));

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
        this.loadData = false;
        loadPrimaryKeysList(this.databaseName);
    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {
        if(this.loadData) {
            try {
                tables.clear();
                JSONArray jsonArray = new JSONArray(jsonArrayData);
                for (int i = 0; i < jsonArray.length(); i++)
                    tables.add(jsonArray.getJSONObject(i).getString("Name").toString());

                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                // for Google Analytics
            }
        }else{
            try {
                this.primaryKeysModel = PrimaryKeysModel.getInstance();
                this.primaryKeysModel.clearKeys();

                JSONArray jsonArray = new JSONArray(jsonArrayData);
                for (int i = 0; i < jsonArray.length(); i++){
                    String tableName = jsonArray.getJSONObject(i).getString("TABLE_NAME").toString();
                    String columnName = jsonArray.getJSONObject(i).getString("COLUMN_NAME").toString();

                    this.primaryKeysModel.addKey(tableName, columnName);
                }

            } catch (JSONException e) {
                // for Google Analytics
            }

            this.loadData = true;
            loadTablesList(this.databaseName);
        }
    }
}
