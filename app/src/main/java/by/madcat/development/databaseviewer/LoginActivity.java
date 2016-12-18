package by.madcat.development.databaseviewer;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import by.madcat.development.databaseviewer.BroadcastReceivers.DataReceiver;
import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.ConnectData.ConnectModel;
import by.madcat.development.databaseviewer.Requests.RequestService;

public class LoginActivity extends AppCompatActivity implements DataReceiver{

    private Button connectBtn;
    private AVLoadingIndicatorView progressConnect;
    private TextView connectName;
    private TextView serverIpAdress;
    private TextView userName;
    private TextView userPassword;

    private ServerRequestBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressConnect = (AVLoadingIndicatorView)findViewById(R.id.progressConnect);
        progressConnect.hide();

        connectName = (TextView)findViewById(R.id.connectName);
        serverIpAdress = (TextView)findViewById(R.id.serverIpAdress);
        userName = (TextView)findViewById(R.id.userName);
        userPassword = (TextView)findViewById(R.id.userPassword);

        connectBtn = (Button)findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressConnect.show();
                connectBtn.setEnabled(false);

                ConnectModel model = ConnectModel.getInstance(serverIpAdress.getText().toString(),
                        userName.getText().toString(),
                        userPassword.getText().toString());

                Intent intent = new Intent(LoginActivity.this, RequestService.class);
                startService(intent);
            }
        });

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new ServerRequestBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void sendErrorMessage(String errorMessage) {
        progressConnect.hide();
        connectBtn.setEnabled(true);

        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendConnectConfirmation() {
        connectBtn.setEnabled(true);
        progressConnect.hide();

        Toast.makeText(getApplicationContext(), "connection confirm", Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendDataFromServer(String jsonArrayData) {

    }
}
