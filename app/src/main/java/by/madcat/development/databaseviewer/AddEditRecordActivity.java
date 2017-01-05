package by.madcat.development.databaseviewer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;

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

        action = getIntent().getIntExtra(RECORD_ACTION, 1);
        tableName = getIntent().getStringExtra(TABLE_NAME);
        databaseName = getIntent().getStringExtra(DATABASE_NAME);
        primaryKeyValue = getIntent().getStringExtra(PRIMARY_KEY_VALUE);
        primaryKeyFieldName = getIntent().getStringExtra(PRIMARY_KEY_FIELD_NAME);

        recordLayout = (TableLayout)findViewById(R.id.record);
        insertRecordButton = (Button)findViewById(R.id.insertRecordButton);

        if(action == RECORD_ADD){
            insertRecordButton.setText("insert");
            setTitle("Add new record in '" + tableName + "' table");
        }else if(action == RECORD_EDIT){
            insertRecordButton.setText("update");
            setTitle("Edit record in '" + tableName + "' table where '" + primaryKeyFieldName + "' = '" + primaryKeyValue + "'");
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
    public void sendErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendConnectConfirmation() {

    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {

    }

    @Override
    public void sendQueryExecutedNoResult() {

    }
}
