package by.madcat.development.databaseviewer.ActivitiesUI;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.Adapters.DatabasesListRecyclerViewAdapter;
import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;

public class DataBasesListActivity extends AbstractApplicationActivity implements DataReceiver {

    private RecyclerView databasesList;
    private DatabasesListRecyclerViewAdapter adapter;
    private ArrayList<String> databases;

    private FloatingActionButton databaseAdd;

    private ConnectModel connectModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_bases_list);

        connectModel = ConnectModel.getInstance("", null, "", "");

        databasesList = (RecyclerView)findViewById(R.id.databases_list);
        databasesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        databasesList.setHasFixedSize(true);

        databases = new ArrayList<>();
        adapter = new DatabasesListRecyclerViewAdapter(databases, this);

        databasesList.setAdapter(adapter);

        databaseAdd = (FloatingActionButton) findViewById(R.id.database_add);
        databaseAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddEditDatabaseActivity.getIntent(
                        getApplicationContext(),
                        AddEditDatabaseActivity.DATABASE_ADD,
                        "");
                startActivity(intent);
            }
        });

        loadDatabasesList();

        setTitle("Databases list of " + connectModel.getQueriesGenerator().getDatabaseType() + " server on " + ConnectModel.getInstance("", null, "", "").getServerIpAdress());
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);

        loadDatabasesList();
    }

    private void loadDatabasesList(){
        connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().getDatabasesList());

        Intent intent = new Intent(DataBasesListActivity.this, RequestService.class);
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
        loadDatabasesList();
    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {
        try {
            databases.clear();
            JSONArray jsonArray = new JSONArray(jsonArrayData);
            for(int i = 0; i < jsonArray.length(); i++)
                databases.add(jsonArray.getJSONObject(i).getString(connectModel.getQueriesGenerator().getDatabaseListKey()).toString());

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            // for Google Analytics
        }
    }
}
