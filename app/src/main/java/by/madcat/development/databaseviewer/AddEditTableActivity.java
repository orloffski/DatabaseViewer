package by.madcat.development.databaseviewer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import by.madcat.development.databaseviewer.ConnectData.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesList;

public class AddEditTableActivity extends AppCompatActivity {

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
                        connectModel.setUserRequestToServer(String.format(QueriesList.TABLE_ADD, dbName, tableNameEditText.getText().toString()));
                        break;
                    case TABLE_EDIT:

                        break;
                }

                startService(intent);
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
}
