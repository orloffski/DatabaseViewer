package by.madcat.development.databaseviewer.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServerRequestBroadcastReceiver extends BroadcastReceiver{

    public static final String BROADCAST_ACTION = "by.madcat.development.databaseviewer";

    public static final int BROADCASTRECEIVER_STATUS_ERROR = 0;
    public static final int BROADCASTRECEIVER_STATUS_CONNECT = 1;
    public static final int BROADCASTRECEIVER_STATUS_SEND_DATA = 2;

    public static final String BROADCASTRECEIVER_DATA = "broadcastreceiver_data";
    public static final String BROADCASTRECEIVER_STATUS = "broadcastreceiver_status";

    private DataReceiver dataReceiver;

    public ServerRequestBroadcastReceiver(DataReceiver dataReceiver){
        this.dataReceiver = dataReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String jsonArrayData = "";

        int status = intent.getIntExtra(BROADCASTRECEIVER_STATUS, 0);

        if(status != 1)
            jsonArrayData = intent.getStringExtra(BROADCASTRECEIVER_DATA);

        switch (status){
            case BROADCASTRECEIVER_STATUS_ERROR:
                this.dataReceiver.sendErrorMessage(jsonArrayData);
                break;
            case BROADCASTRECEIVER_STATUS_CONNECT:
                this.dataReceiver.sendConnectConfirmation();
                break;
            case BROADCASTRECEIVER_STATUS_SEND_DATA:
                this.dataReceiver.sendDataFromServer(jsonArrayData);
                break;
        }
    }
}
