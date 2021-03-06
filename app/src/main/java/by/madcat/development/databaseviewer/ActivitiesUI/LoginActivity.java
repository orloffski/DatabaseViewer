package by.madcat.development.databaseviewer.ActivitiesUI;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.DatabasesTypes;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.QueriesGeneratorFactory;

public class LoginActivity extends AbstractApplicationActivity implements DataReceiver {

    private Button connectBtn;
    private AVLoadingIndicatorView progressConnect;
    private TextView connectName;
    private TextView serverIpAdress;
    private Spinner databaseType;
    private TextView userName;
    private TextView userPassword;
    private CheckBox passwordChb;

    private ConnectModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressConnect = (AVLoadingIndicatorView)findViewById(R.id.progressConnect);

        connectName = (TextView)findViewById(R.id.connectName);
        serverIpAdress = (TextView)findViewById(R.id.serverIpAdress);
        userName = (TextView)findViewById(R.id.userName);
        userPassword = (TextView)findViewById(R.id.userPassword);
        databaseType = (Spinner)findViewById(R.id.databaseType);
        databaseType.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, DatabasesTypes.values()));

        passwordChb = (CheckBox)findViewById(R.id.passwordChb);
        passwordChb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(passwordChb.isChecked())
                    userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        connectBtn = (Button)findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressConnect.show();
                connectBtn.setEnabled(false);

                try {
                    model = ConnectModel.updateInstance(serverIpAdress.getText().toString(),
                            QueriesGeneratorFactory.getGenerator((DatabasesTypes)databaseType.getSelectedItem()),
                            userName.getText().toString(),
                            userPassword.getText().toString());
                } catch (Exception e) {
                    // to google analytics
                }
                model.setUserRequestToServer(null);

                Intent intent = new Intent(LoginActivity.this, RequestService.class);
                intent.putExtra(RequestService.SERVER_IP_ADRESS, model.getServerIpAdress());
                intent.putExtra(RequestService.DATABASE_TYPE, databaseType.getSelectedItem().toString());
                intent.putExtra(RequestService.USER_NAME, model.getUserName());
                intent.putExtra(RequestService.USER_PASSWORD, model.getUserPassword());
                intent.putExtra(RequestService.EXECUTE_MODEL, 0);
                startService(intent);
            }
        });

        setTitle("Database viewer v.1.0");
    }

    @Override
    protected void onResume() {
        super.onStart();

        progressConnect.hide();
        connectBtn.setEnabled(true);

        try {
            model = ConnectModel.updateInstance(serverIpAdress.getText().toString(),
                    QueriesGeneratorFactory.getGenerator((DatabasesTypes)databaseType.getSelectedItem()),
                    userName.getText().toString(),
                    userPassword.getText().toString());
        } catch (Exception e) {
            // to google analytics
        }
        model.setUserRequestToServer(null);

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        broadcastReceiver.register(getApplicationContext(), intentFilter);
    }

    @Override
    public void sendErrorMessage(String errorMessage) {
        progressConnect.hide();
        connectBtn.setEnabled(true);

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendConnectConfirmation() {
        Intent intent = new Intent(getApplicationContext(), DataBasesListActivity.class);
        startActivity(intent);
    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {

    }

    @Override
    public void sendQueryExecutedNoResult() {

    }
}
