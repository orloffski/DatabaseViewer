package by.madcat.development.databaseviewer;

import android.support.v7.app.AppCompatActivity;

import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;

public abstract class AbstractActivityApplicationActivity extends AppCompatActivity {

    protected ServerRequestBroadcastReceiver broadcastReceiver;

    @Override
    protected void onPause() {
        super.onPause();

        broadcastReceiver.unregister(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        broadcastReceiver.unregister(getApplicationContext());
    }
}
