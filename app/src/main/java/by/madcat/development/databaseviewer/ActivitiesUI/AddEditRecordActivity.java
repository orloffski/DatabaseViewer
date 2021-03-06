package by.madcat.development.databaseviewer.ActivitiesUI;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Models.PrimaryKeysModel;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;
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

        connectModel = ConnectModel.getInstance("", null, "", "");

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
                Intent intent = new Intent(AddEditRecordActivity.this, RequestService.class);
                intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
                intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
                intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
                intent.putExtra(RequestService.EXECUTE_MODEL, 1);

                switch (action){
                    case RECORD_ADD:
                        connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().insertRecords(databaseName, tableName, getFieldsValuesToInsert()));
                        break;
                    case RECORD_EDIT:
                        connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().updateRecord(databaseName, tableName, getFieldsValuesToUpdate(), getRecordPrimaryKeyString()));
                        break;
                }

                startService(intent);
            }
        });

        Intent intent = new Intent(AddEditRecordActivity.this, RequestService.class);
        intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
        intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
        intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
        intent.putExtra(RequestService.EXECUTE_MODEL, 2);
        connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().getTableMetadata(databaseName, tableName));
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
        connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().getRecord(databaseName, tableName, primaryKeyFieldName, primaryKeyValue));
        startService(intent);
    }

    private String getRecordPrimaryKeyString(){
        return primaryKeyFieldName + " = '" + primaryKeyValue + "'";
    }

    private String getFieldsValuesToUpdate(){
        StringBuilder stringBuilder = new StringBuilder();

        int count = recordLayout.getChildCount();

        for(int i = 0; i < count; i++){
            TableRow row = (TableRow)recordLayout.getChildAt(i);

            TextView fieldView = (TextView)row.getChildAt(0);

            stringBuilder.append(fieldView.getText().toString());
            stringBuilder.append("=").append("'");

            if(row.getChildAt(1) instanceof EditText) {
                EditText fieldValue = (EditText) row.getChildAt(1);

                stringBuilder.append(fieldValue.getText().toString());
                stringBuilder.append("'");
            }else if(row.getChildAt(1) instanceof CheckBox){
                CheckBox fieldValue = (CheckBox)row.getChildAt(1);
                int value = fieldValue.isChecked() ? 1 : 0;

                stringBuilder.append(String.valueOf(value));
                stringBuilder.append("'");
            }

            if(i != count - 1)
                stringBuilder.append(", ");
        }

        return stringBuilder.toString();
    }

    private String getFieldsValuesToInsert(){
        StringBuilder stringBuilder = new StringBuilder();

        int count = recordLayout.getChildCount();

        for(int i = 0; i < count; i++){
            TableRow row = (TableRow) recordLayout.getChildAt(i);

            if(row.getChildAt(1) instanceof EditText) {
                EditText fieldValue = (EditText) row.getChildAt(1);

                stringBuilder.append("'");
                stringBuilder.append(fieldValue.getText().toString());
                stringBuilder.append("'");
            }else if(row.getChildAt(1) instanceof CheckBox){
                CheckBox fieldValue = (CheckBox) row.getChildAt(1);
                int value = fieldValue.isChecked() ? 1 : 0;

                stringBuilder.append("'");
                stringBuilder.append(String.valueOf(value));
                stringBuilder.append("'");
            }

            if(i != count - 1)
                stringBuilder.append(", ");
        }

        return stringBuilder.toString();
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

            try {
                JSONArray jsonArray = new JSONArray(jsonArrayData);
                ViewGenerator.fillRecordView(recordLayout, tableMetadata, jsonArray);
            } catch (JSONException e) {
                // for Google Analytics
            }
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
        finish();
    }
}
