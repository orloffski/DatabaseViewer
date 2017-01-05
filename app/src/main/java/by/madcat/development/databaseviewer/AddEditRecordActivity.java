package by.madcat.development.databaseviewer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Models.PrimaryKeysModel;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQLQueriesGenerator;
import by.madcat.development.databaseviewer.Utils.SqlJsonUtils;
import by.madcat.development.databaseviewer.Utils.ViewGenerator;

public class AddEditRecordActivity extends AbstractApplicationActivity implements DataReceiver {

    public static final String RECORD_ACTION = "record_action";
    public static final String TABLE_NAME = "table_name";
    public static final String DATABASE_NAME = "database_name";
    public static final String PRIMARY_KEY_VALUE = "primary_key_value";
    public static final String PRIMARY_KEY_FIELD_NAME = "primary_key_field_name";

    public static final int RECORD_ADD = 1;
    public static final int RECORD_EDIT = 2;

    private int action;
    private String tableName;
    private String databaseName;
    private ConnectModel connectModel;
    private Button insertRecordButton;
    private TableLayout recordLayout;
    private String primaryKeyValue;
    private String primaryKeyFieldName;
    private TableMetadataModel tableMetadata;
    private boolean loadData;

    public static Intent getIntent(Context context, int recordAction, String tableName, String databaseName, String primaryKeyValue, String primaryKeyFieldName){
        Intent intent = new Intent(context, AddEditRecordActivity.class);
        intent.putExtra(RECORD_ACTION, recordAction);
        intent.putExtra(TABLE_NAME, tableName);
        intent.putExtra(DATABASE_NAME, databaseName);
        intent.putExtra(PRIMARY_KEY_VALUE, primaryKeyValue);
        intent.putExtra(PRIMARY_KEY_FIELD_NAME, primaryKeyFieldName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_record);

        connectModel = ConnectModel.getInstance("", "", "");

        this.loadData = false;

        action = getIntent().getIntExtra(RECORD_ACTION, 1);
        tableName = getIntent().getStringExtra(TABLE_NAME);
        databaseName = getIntent().getStringExtra(DATABASE_NAME);
        primaryKeyValue = getIntent().getStringExtra(PRIMARY_KEY_VALUE);
        primaryKeyFieldName = getIntent().getStringExtra(PRIMARY_KEY_FIELD_NAME);

        recordLayout = (TableLayout)findViewById(R.id.record);
        insertRecordButton = (Button)findViewById(R.id.insertRecordButton);
        insertRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Intent intent = new Intent(AddEditRecordActivity.this, RequestService.class);
        intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
        intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
        intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
        intent.putExtra(RequestService.EXECUTE_MODEL, 2);
        connectModel.setUserRequestToServer(MSSQLQueriesGenerator.getTableMetadata(databaseName, tableName));
        startService(intent);

        switch (action){
            case RECORD_ADD:
                insertRecordButton.setText("insert");
                setTitle("Add new record in '" + tableName + "' table");
                break;
            case RECORD_EDIT:
                insertRecordButton.setText("update");
                setTitle("Edit record in '" + tableName + "' table where '" + primaryKeyFieldName + "' = '" + primaryKeyValue + "'");
                break;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);
    }

    private void loadRecord(){
        Intent intent = new Intent(AddEditRecordActivity.this, RequestService.class);
        intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
        intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
        intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
        intent.putExtra(RequestService.EXECUTE_MODEL, 2);
        connectModel.setUserRequestToServer(MSSQLQueriesGenerator.getRecord(databaseName, tableName, primaryKeyFieldName, primaryKeyValue));
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
    public void sendDataFromServer(String jsonArrayData) {
        if(this.loadData){

        }else{
            tableMetadata = SqlJsonUtils.createTableMetadata(jsonArrayData, tableName, PrimaryKeysModel.getInstance());

            ViewGenerator.createRecordView(this, recordLayout, tableMetadata);

            if(action == RECORD_EDIT){
                this.loadData = true;
                loadRecord();
            }
        }
    }

    @Override
    public void sendQueryExecutedNoResult() {

    }
}
