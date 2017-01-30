package by.madcat.development.databaseviewer.ActivitiesUI;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Models.PrimaryKeysModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.SqlJsonUtils;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQL.SqlTypes;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.Utils.ViewGenerator;

public class AddEditTableActivity extends AbstractApplicationActivity implements DataReceiver, AddEditFieldView.FieldDeleted {

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

    private TableMetadataModel oldTable;
    private TableMetadataModel newTable;

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

        connectModel = ConnectModel.getInstance("", null, "", "");

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
                        checkChangesNewTableMetadata();

                        if(checkTableToEdit())
                            connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().changeTable(
                                    oldTable, newTable, dbName, tableName));
                        else
                            finish();

                        break;
                }

                //Log.d("payment", connectModel.getUserRequestToServer());
                startService(intent);
            }
        });


        addFieldButton = (Button)findViewById(R.id.add_field_button);
        addFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGenerator.addNewFieldInMainView(getApplicationContext(), fieldsLinearLayout, AddEditTableActivity.this);
            }
        });

        setTitle(action);

        if(action == TABLE_EDIT){
            getTableMetadata();
        }
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

    private boolean checkTableToEdit(){
        if(!oldTable.equals(newTable)) {
            return true;
        }

        return false;
    }

    private void checkChangesNewTableMetadata(){
        newTable.setTableName(tableNameEditText.getText().toString());

        int fieldsLinearLayoutCount = fieldsLinearLayout.getChildCount();

        for(int i = 0; i < fieldsLinearLayoutCount; i++){
            LinearLayout field = (LinearLayout) fieldsLinearLayout.getChildAt(i);

            String fieldName = ((EditText)field.findViewById(R.id.fieldName)).getText().toString();

            SqlTypes[] types = SqlTypes.values();
            int position = ((Spinner)field.findViewById(R.id.typesSpinner)).getSelectedItemPosition();
            String type = types[position].toString();
            SqlTypes fieldType = SqlTypes.valueOf(type);

            int length = 0;
            if(types[position].equals(SqlTypes.VARCHAR))
                length = ((EditText)field.findViewById(R.id.fieldLenght)).getText().toString().equals("") ? 0 : Integer.parseInt(((EditText)field.findViewById(R.id.fieldLenght)).getText().toString());

            boolean primaryKey;
            if(((CheckBox)field.findViewById(R.id.primaryKeyChb)).isChecked())
                primaryKey = true;
            else
                primaryKey = false;

            if(newTable.getFieldByName(fieldName) != null)
                newTable.updateField(fieldName, fieldType, length);
            else
                newTable.addNewField(fieldName, fieldType, length, primaryKey);
        }
    }

    private void createTable(){
        StringBuilder stringBuilder = new StringBuilder();
        String primaryKey = "";
        int fieldsLinearLayoutCount = fieldsLinearLayout.getChildCount();

        for(int i = 0; i < fieldsLinearLayoutCount; i++){
            LinearLayout field = (LinearLayout) fieldsLinearLayout.getChildAt(i);

            String fieldName = ((EditText)field.findViewById(R.id.fieldName)).getText().toString();
            stringBuilder.append(fieldName).append(" ");

            SqlTypes[] types = SqlTypes.values();
            int position = ((Spinner)field.findViewById(R.id.typesSpinner)).getSelectedItemPosition();
            String fieldType = types[position].toString();
            if(types[position].equals(SqlTypes.VARCHAR)){
                int length = Integer.parseInt(((EditText)field.findViewById(R.id.fieldLenght)).getText().toString());
                stringBuilder.append(fieldType).append("(").append(String.valueOf(length)).append(")");
            }else{
                stringBuilder.append(fieldType);
            }

            if(((CheckBox)field.findViewById(R.id.primaryKeyChb)).isChecked())
                primaryKey = connectModel.getQueriesGenerator().getPrimaryKeyPart(fieldName);

            if(i != fieldsLinearLayoutCount - 1 || !primaryKey.equals(""))
                stringBuilder.append(", ");
        }

        if(!primaryKey.equals(""))
            stringBuilder.append(primaryKey);

        connectModel.setUserRequestToServer(
                connectModel.getQueriesGenerator().createTable(dbName, tableNameEditText.getText().toString(), stringBuilder.toString()));
        }

    private void getTableMetadata(){
        Intent intent = new Intent(AddEditTableActivity.this, RequestService.class);
        intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
        intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
        intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
        intent.putExtra(RequestService.EXECUTE_MODEL, 2);

        connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().getTableMetadata(dbName, tableName));

        startService(intent);
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
        oldTable = SqlJsonUtils.createTableMetadata(jsonArrayData, tableName, PrimaryKeysModel.getInstance());
        newTable = oldTable.clone();
        ViewGenerator.addIssetFieldsInMainView(getApplicationContext(), fieldsLinearLayout, oldTable.getFieldsList(), this);
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

    @Override
    public void fieldToDelete(String fieldName) {
        if(newTable.getFieldByName(fieldName) != null)
            newTable.setFieldToDeleteByName(fieldName);
    }
}
