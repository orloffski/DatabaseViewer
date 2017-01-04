package by.madcat.development.databaseviewer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TableLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.Adapters.RecordsListAdapter;
import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQLQueriesGenerator;

public class RecordsListActivity extends AbstractApplicationActivity implements DataReceiver {
    public static final String DATABASE_NAME = "database_name";
    public static final String TABLE_NAME = "table_name";

    private String databaseName;
    private String tableName;

    private ArrayList<String[]> records;

    private TableLayout recordsList;
    private RecordsListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_list);

        this.databaseName = getIntent().getStringExtra(DATABASE_NAME);
        this.tableName = getIntent().getStringExtra(TABLE_NAME);

        records = new ArrayList<>();
        recordsList = (TableLayout) findViewById(R.id.recordsTable);
        adapter = new RecordsListAdapter(this, records);

        loadRecordsList(this.databaseName, this.tableName);

        setTitle("Records list of '" + this.tableName + "' table");
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);

        loadRecordsList(this.databaseName, this.tableName);
    }

    public static Intent getIntent(Context context, String databaseName, String tableName){
        Intent intent = new Intent(context, RecordsListActivity.class);
        intent.putExtra(DATABASE_NAME, databaseName);
        intent.putExtra(TABLE_NAME, tableName);
        return intent;
    }

    @Override
    public void sendErrorMessage(String errorMessage) {

    }

    @Override
    public void sendConnectConfirmation() {

    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {
        try {
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> tmp = new ArrayList<>();

            records.clear();
            JSONArray jsonArray = new JSONArray(jsonArrayData);
            JSONArray key =jsonArray.getJSONObject(0).names();

            for(int i = 0; i < key.length(); i++)
                keys.add(key.getString(i));

            records.add(keys.toArray(new String[keys.size()]));

            for(int i = 0; i < jsonArray.length(); i++) {
                for (int j = 0; j < keys.size(); j++) {
                    tmp.add(jsonArray.getJSONObject(i).getString(keys.get(j)).toString());
                }

                records.add(tmp.toArray(new String[tmp.size()]));
                tmp.clear();
            }

            createRecordsList();
        } catch (JSONException e) {
            // for Google Analytics
        }
    }

    @Override
    public void sendQueryExecutedNoResult() {

    }

    private void loadRecordsList(String databaseName, String tableName){
        ConnectModel model = ConnectModel.getInstance("", "", "");
        model.setUserRequestToServer(MSSQLQueriesGenerator.getRecordsList(databaseName, tableName));

        Intent intent = new Intent(RecordsListActivity.this, RequestService.class);
        intent.putExtra(RequestService.EXECUTE_MODEL, 2);
        startService(intent);
    }

    private void createRecordsList(){
        recordsList.removeAllViews();

        int count = adapter.getCount();
        for(int i = 0; i < count; i++){
            recordsList.addView(adapter.getView(i, null, recordsList));
        }
    }
}