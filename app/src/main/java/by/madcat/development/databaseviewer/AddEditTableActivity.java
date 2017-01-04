package by.madcat.development.databaseviewer;

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

import org.json.JSONArray;
import org.json.JSONException;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.QueriesGenerators.MSSQLQueriesPartsList;
import by.madcat.development.databaseviewer.Utils.SqlTypes;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.Utils.ViewGenerator;

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
                        if(checkTableToEdit())
                            createTransactionOfChanges();
                        else
                            finish();

                        break;
                }

                startService(intent);
            }
        });


        addFieldButton = (Button)findViewById(R.id.add_field_button);
        addFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGenerator.addNewFieldInMainView(getApplicationContext(), fieldsLinearLayout);
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
        createNewTableMetadata();

        if(!oldTable.equals(newTable)) {
            return true;
        }

        return false;
    }

    private void createTransactionOfChanges(){
        int counter;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MSSQLQueriesPartsList.BEGIN_TRANSACTION);
        stringBuilder.append(String.format(MSSQLQueriesPartsList.DB_SELECT, dbName));

        if(!oldTable.getTableName().equals(newTable.getTableName()))
            stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_NAME, tableName, newTable.getTableName()));

        counter = oldTable.getFieldsList().size();

        if(counter > newTable.getFieldsList().size()) {
            counter = newTable.getFieldsList().size();
        }

        for(int i = 0; i < counter; i++){
            if(!oldTable.getFieldsList().get(i).getFieldName().equals(newTable.getFieldsList().get(i).getFieldName())){
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_CHANGE_FIELD_RENAME,
                        newTable.getTableName(),
                        oldTable.getFieldsList().get(i).getFieldName(),
                        newTable.getFieldsList().get(i).getFieldName()));
            }

            if(!oldTable.getFieldsList().get(i).getType().equals(newTable.getFieldsList().get(i).getType())){
                String type = "";
                if(newTable.getFieldsList().get(i).getType().equals(SqlTypes.VARCHAR)){
                    type = SqlTypes.VARCHAR.toString() + "(" + newTable.getFieldsList().get(i).getLength() + ")";
                }else{
                    type = newTable.getFieldsList().get(i).getType().toString();
                }

                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_CHANGE_FIELD_TYPE,
                        newTable.getTableName(),
                        newTable.getFieldsList().get(i).getFieldName(),
                        type));
            }

            if(oldTable.getFieldsList().get(i).getLength() != newTable.getFieldsList().get(i).getLength() && newTable.getFieldsList().get(i).getType().equals(SqlTypes.VARCHAR)){
                String length = newTable.getFieldsList().get(i).getType().toString() +
                        "(" +
                        newTable.getFieldsList().get(i).getLength() +
                        ")";
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_CHANGE_FIELD_TYPE,
                        newTable.getTableName(),
                        newTable.getFieldsList().get(i).getFieldName(),
                        length));
            }
        }

        if(newTable.getFieldsList().size() < oldTable.getFieldsList().size()){
            for(int i = counter; i < oldTable.getFieldsList().size(); i++){
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT, newTable.getTableName()));
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_DELETE_FIELD, oldTable.getFieldsList().get(i).getFieldName()));
            }
        }else{
            for(int i = counter; i < newTable.getFieldsList().size(); i++){
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT, newTable.getTableName()));
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_ADD_FIELD,
                        newTable.getFieldsList().get(i).getFieldName(),
                        newTable.getFieldsList().get(i).getType()));
            }
        }

        stringBuilder.append(MSSQLQueriesPartsList.COMMIT_TRANSACTION);

        connectModel.setUserRequestToServer(stringBuilder.toString());
    }

    private void createNewTableMetadata(){
        newTable = new TableMetadataModel(tableNameEditText.getText().toString());

        int fieldsLinearLayoutCount = fieldsLinearLayout.getChildCount();

        for(int i = 0; i < fieldsLinearLayoutCount; i++){
            LinearLayout field = (LinearLayout) fieldsLinearLayout.getChildAt(i);

            String fieldName = ((EditText)field.getChildAt(0)).getText().toString();

            SqlTypes[] types = SqlTypes.values();
            int position = ((Spinner)field.getChildAt(2)).getSelectedItemPosition();
            String type = types[position].toString();
            SqlTypes fieldType = SqlTypes.valueOf(type);

            int length = 0;
            if(types[position].equals(SqlTypes.VARCHAR))
                length = ((EditText)field.getChildAt(1)).getText().toString().equals("") ? 0 : Integer.parseInt(((EditText)field.getChildAt(1)).getText().toString());

            boolean primaryKey;
            if(((CheckBox)field.getChildAt(3)).isChecked())
                primaryKey = true;
            else
                primaryKey = false;

            newTable.addNewField(fieldName, fieldType, length, primaryKey);
        }
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
            int position = ((Spinner)field.getChildAt(2)).getSelectedItemPosition();
            String fieldType = types[position].toString();
            if(types[position].equals(SqlTypes.VARCHAR)){
                int length = Integer.parseInt(((EditText)field.getChildAt(1)).getText().toString());
                stringBuilder.append(fieldType).append("(").append(String.valueOf(length)).append(")");
            }else{
                stringBuilder.append(fieldType);
            }

            if(((CheckBox)field.getChildAt(3)).isChecked())
                primaryKey = String.format(MSSQLQueriesPartsList.TABLE_EDIT_PRIMARY_KEY, fieldName);

            if(i != fieldsLinearLayoutCount - 1 || !primaryKey.equals(""))
                stringBuilder.append(", ");
        }

        if(!primaryKey.equals(""))
            stringBuilder.append(primaryKey);


        connectModel.setUserRequestToServer(
                String.format(MSSQLQueriesPartsList.TABLE_ADD, dbName, tableNameEditText.getText().toString(), stringBuilder.toString()));
        }

    private void getTableMetadata(){
        Intent intent = new Intent(AddEditTableActivity.this, RequestService.class);
        intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
        intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
        intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
        intent.putExtra(RequestService.EXECUTE_MODEL, 2);

        connectModel.setUserRequestToServer(
                String.format(MSSQLQueriesPartsList.TABLE_METADATA, dbName, tableName));

        startService(intent);
    }

    public void createOldTableMetadata(String jsonArrayData){
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayData);
            oldTable = new TableMetadataModel(tableName);

            for(int i = 0; i < jsonArray.length(); i++){
                String fieldName = jsonArray.getJSONObject(i).getString("COLUMN_NAME").toString();
                SqlTypes type = SqlTypes.valueOf(jsonArray.getJSONObject(i).getString("DATA_TYPE").toString().toUpperCase());
                int length = jsonArray.getJSONObject(i).getString("CHARACTER_MAXIMUM_LENGTH").toString().equals("") ? 0 :
                        Integer.parseInt(jsonArray.getJSONObject(i).getString("CHARACTER_MAXIMUM_LENGTH").toString());
                String PK = jsonArray.getJSONObject(i).getString("ORDINAL_POSITION").toString();

                boolean isKey = PK.equals("") ? false : true;

                oldTable.addNewField(fieldName, type, length, isKey);
            }
            createIssetFields();
        } catch (JSONException e) {
            // for Google Analytics
        }
    }

    public void createIssetFields(){
        ViewGenerator.addIssetFieldsInMainView(getApplicationContext(), fieldsLinearLayout, oldTable.getFieldsList());
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
        createOldTableMetadata(jsonArrayData);
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
