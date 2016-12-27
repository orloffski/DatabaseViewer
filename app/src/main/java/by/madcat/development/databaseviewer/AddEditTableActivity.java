package by.madcat.development.databaseviewer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.ConnectData.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesList;
import by.madcat.development.databaseviewer.Utils.ViewGenerator;
import by.madcat.development.databaseviewer.SupportClasses.*;

public class AddEditTableActivity extends AbstractApplicationActivity implements DataReceiver {

    public static final String TABLE_ACTION = "table_action";
    public static final String TABLE_NAME = "table_name";
    public static final String DATABASE_NAME = "database_name";

    public static final int TABLE_ADD = 1;
    public static final int TABLE_EDIT = 2;

    private ConnectModel connectModel;

    private int action;
    private String tableName;
    private String dbName;

    private EditText tableNameEditText;
    private Button saveButton;
    private Button addFieldButton;
    private LinearLayout fieldsLinearLayout;

    public static Intent getIntent(Context context, int tableAction, String tableName, String dbName){
        Intent intent = new Intent(context, AddEditTableActivity.class);
        intent.putExtra(TABLE_ACTION, tableAction);
        intent.putExtra(TABLE_NAME, tableName);
        intent.putExtra(DATABASE_NAME, dbName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_table);

        connectModel = ConnectModel.getInstance("", "", "");

        action = getIntent().getIntExtra(TABLE_ACTION, 1);
        tableName = getIntent().getStringExtra(TABLE_NAME);
        dbName = getIntent().getStringExtra(DATABASE_NAME);

        fieldsLinearLayout = (LinearLayout)findViewById(R.id.fieldsLinearLayout);

        tableNameEditText = (EditText)findViewById(R.id.table_name);
        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEditTableActivity.this, RequestService.class);
                intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
                intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
                intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
                intent.putExtra(RequestService.EXECUTE_MODEL, 1);

                switch (action){
                    case TABLE_ADD:
                        createTable();
                        break;
                    case TABLE_EDIT:

                        break;
                }

                startService(intent);
            }
        });


        addFieldButton = (Button)findViewById(R.id.add_field_button);
        addFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGenerator.generateNewFieldView(getApplicationContext(), fieldsLinearLayout);
            }
        });

        setTitle(action);
    }

    @Override
    public void setTitle(int action){
        switch (action){
            case TABLE_ADD:
                setTitle("Add new table");
                break;
            case TABLE_EDIT:
                setTitle("Edit table '" + tableName + "'");
                loadElements();
                break;
        }
    }

    private void loadElements(){
        this.tableNameEditText.setText(tableName);
    }

    private void createTable(){
        StringBuilder stringBuilder = new StringBuilder();
        String primaryKey = "";
        int fieldsLinearLayoutCount = fieldsLinearLayout.getChildCount();

        for(int i = 0; i < fieldsLinearLayoutCount; i++){
            LinearLayout field = (LinearLayout) fieldsLinearLayout.getChildAt(i);

            String fieldName = ((EditText)field.getChildAt(0)).getText().toString();
            stringBuilder.append(fieldName).append(" ");

            SqlTypes[] types = SqlTypes.values();
            int position = ((Spinner)field.getChildAt(1)).getSelectedItemPosition();
            String fieldType = types[position].toString();
            if(types[position].equals(SqlTypes.VARCHAR)){
                if(((CheckBox)field.getChildAt(3)).isChecked())
                    primaryKey = String.format(QueriesList.TABLE_EDIT_PRIMARY_KEY, fieldName);
                int length = Integer.parseInt(((EditText)field.getChildAt(2)).getText().toString());
                stringBuilder.append(fieldType).append("(").append(String.valueOf(length)).append(")");
            }else{
                if(((CheckBox)field.getChildAt(2)).isChecked())
                    primaryKey = String.format(QueriesList.TABLE_EDIT_PRIMARY_KEY, fieldName);
                stringBuilder.append(fieldType);
            }

            if(i != fieldsLinearLayoutCount - 1 || !primaryKey.equals(""))
                stringBuilder.append(", ");
        }

        if(!primaryKey.equals(""))
            stringBuilder.append(primaryKey);


        connectModel.setUserRequestToServer(
                String.format(QueriesList.TABLE_ADD, dbName, tableNameEditText.getText().toString(), stringBuilder.toString()));
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
        switch (action){
            case TABLE_ADD:
                Toast.makeText(getApplicationContext(),
                        "Table " + tableNameEditText.getText().toString() + " created", Toast.LENGTH_SHORT).show();
                break;
            case TABLE_EDIT:
                Toast.makeText(getApplicationContext(),
                        "Table " + tableNameEditText.getText().toString() + " changed", Toast.LENGTH_SHORT).show();
                break;
        }
        this.finish();
    }
}
