package by.madcat.development.databaseviewer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.ConnectData.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesList;

public class AddEditDatabaseActivityApplicationActivity extends AbstractActivityApplicationActivity implements DataReceiver {

    public static final String DATABASE_ACTION = "database_action";
    public static final String DATABASE_NAME = "database_name";

    public static final int DATABASE_ADD = 1;
    public static final int DATABASE_EDIT = 2;

    private ConnectModel connectModel;
    private int action;
    private String dbName;

    private EditText databaseName;
    private Button saveButton;

    public static Intent getIntent(Context context, int databaseAction, String databaseName){
        Intent intent = new Intent(context, AddEditDatabaseActivityApplicationActivity.class);
        intent.putExtra(DATABASE_ACTION, databaseAction);
        intent.putExtra(DATABASE_NAME, databaseName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_database);

        action = getIntent().getIntExtra(DATABASE_ACTION, 1);
        dbName = getIntent().getStringExtra(DATABASE_NAME);

        connectModel = ConnectModel.getInstance("", "", "");

        databaseName = (EditText)findViewById(R.id.database_name);
        if(action == DATABASE_EDIT)
            databaseName.setText(dbName);

        saveButton = (Button)findViewById(R.id.save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEditDatabaseActivityApplicationActivity.this, RequestService.class);
                intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
                intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
                intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
                intent.putExtra(RequestService.EXECUTE_MODEL, false);

                switch (action){
                    case DATABASE_ADD:
                        connectModel.setUserRequestToServer(QueriesList.DATABASE_ADD + databaseName.getText().toString());
                        break;
                    case DATABASE_EDIT:
                        connectModel.setUserRequestToServer(
                                String.format(QueriesList.DATABASE_EDIT, dbName, databaseName.getText().toString()));
                        break;
                }

                startService(intent);
            }
        });
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
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendConnectConfirmation() {
        switch (action){
            case DATABASE_ADD:
                Toast.makeText(getApplicationContext(),
                        "Database " + databaseName.getText().toString() + " created", Toast.LENGTH_SHORT).show();
                break;
            case DATABASE_EDIT:
                Toast.makeText(getApplicationContext(),
                        "Database " + dbName + " renemed to " + databaseName.getText().toString(), Toast.LENGTH_SHORT).show();
                break;
        }
        this.finish();
    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {

    }
}
