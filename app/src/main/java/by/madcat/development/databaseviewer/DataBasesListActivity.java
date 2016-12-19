package by.madcat.development.databaseviewer;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.Adapters.RecyclerViewListAdapter;
import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.ConnectData.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesList;

public class DataBasesListActivity extends AppCompatActivity implements DataReceiver {

    private RecyclerView databasesList;
    private RecyclerViewListAdapter adapter;
    private ArrayList<String> databases;

    private ServerRequestBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_bases_list);

        databasesList = (RecyclerView)findViewById(R.id.databases_list);
        databasesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        databasesList.setHasFixedSize(true);

        databases = new ArrayList<>();
        adapter = new RecyclerViewListAdapter(databases, getApplicationContext());

        databasesList.setAdapter(adapter);

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);

        loadDatabasesList();
    }

    @Override
    protected void onPause() {
        super.onPause();

        broadcastReceiver.unregister(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        broadcastReceiver.unregister(getApplicationContext());
    }

    private void loadDatabasesList(){
        ConnectModel model = ConnectModel.getInstance("", "", "");
        model.setUserRequestToServer(QueriesList.DATABASES_LIST_QUERY);

        Intent intent = new Intent(DataBasesListActivity.this, RequestService.class);
        startService(intent);
    }

    @Override
    public void sendErrorMessage(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendConnectConfirmation() {
    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {
        try {
            databases.clear();
            JSONArray jsonArray = new JSONArray(jsonArrayData);
            for(int i = 0; i < jsonArray.length(); i++)
                databases.add(jsonArray.getJSONObject(i).getString("name").toString());

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            // for Google Analytics
        }
    }
}
