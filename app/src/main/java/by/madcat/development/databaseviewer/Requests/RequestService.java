package by.madcat.development.databaseviewer.Requests;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.ConnectData.ConnectModel;
import by.madcat.development.databaseviewer.Utils.SQLUtils;

public class RequestService extends Service {

    public static final String CONNECTION_STRING = "jdbc:jtds:sqlserver://";

    private boolean isError;
    private String errorMessage;
    private ResultSet resultSet;
    private Intent receiveIntent;

    private ConnectModel model;


    public RequestService() {
        model = ConnectModel.getInstance("", "", "");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new AsyncTaskRequest().execute();

        return super.onStartCommand(intent, flags, startId);
    }

    class AsyncTaskRequest extends AsyncTask<Void, Void, Void>{
        private Connection DbConn;
        private Statement statement;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isError = false;
            errorMessage = "";

            DbConn = null;
            statement = null;
            resultSet = null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                DbConn = DriverManager.getConnection(CONNECTION_STRING + model.getServerIpAdress() + ";", getUsersProperties());

                if(DbConn != null && model.getUserRequestToServer() != null){
                    statement = DbConn.createStatement();
                    resultSet = statement.executeQuery(model.getUserRequestToServer());
                }
            } catch (Exception e) {
                if(e instanceof SQLException){
                    isError = true;
                    errorMessage += e.getMessage();
                }else{
                    // for Google Analytics
                }
            }finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (DbConn != null) DbConn.close();
                }catch (SQLException e){
                    isError = true;
                    errorMessage += e.getMessage();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            sendBroadcast();
        }
    }

    private void sendBroadcast(){
        receiveIntent = new Intent(ServerRequestBroadcastReceiver.BROADCAST_ACTION);
        if(isError){
            // send error status and message
            receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS,
                    ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS_ERROR);
            receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_DATA, errorMessage);
        }else{
            if(resultSet != null){
                // send data from server
                try {
                    String jsonArrayString = SQLUtils.createJconArrayFromResultSet(resultSet).toString();
                    receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS,
                            ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS_SEND_DATA);
                    receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_DATA, jsonArrayString);
                } catch (Exception e) {
                    // for Google Analytics
                }
            }else{
                // send confirmation connection
                receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS,
                        ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS_CONNECT);

            }
        }
        sendBroadcast(receiveIntent);
    }

    private Properties getUsersProperties(){
        Properties props = new Properties();
        props.setProperty("user", model.getUserName());
        props.setProperty("password", model.getUserPassword());
        props.setProperty("socketTimeout", "2000");

        return props;
    }
}
