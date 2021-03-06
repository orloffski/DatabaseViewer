package by.madcat.development.databaseviewer.Requests;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import by.madcat.development.databaseviewer.BroadcastReceivers.ServerRequestBroadcastReceiver;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.DatabasesTypes;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.QueriesGeneratorFactory;
import by.madcat.development.databaseviewer.Utils.SqlJsonUtils;

public class RequestService extends Service {

    public static final String SERVER_IP_ADRESS = "server_ip_adress";
    public static final String DATABASE_TYPE = "database_type";
    public static final String USER_NAME = "user_name";
    public static final String USER_PASSWORD = "user_password";
    public static final String EXECUTE_MODEL = "execute_model";
    // 0 - connect
    // 1 - exec query no resultset
    // 2 - get resultset

    private boolean isError;
    private String errorMessage;
    private ResultSet resultSet;
    private Intent receiveIntent;

    private int executeGetResult;

    private ConnectModel connectModel;

    private String jsonArrayString;

    public RequestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            try {
                connectModel = ConnectModel.getInstance(intent.getStringExtra(SERVER_IP_ADRESS),
                        QueriesGeneratorFactory.getGenerator(DatabasesTypes.valueOf(intent.getStringExtra(DATABASE_TYPE))),
                        intent.getStringExtra(USER_NAME),
                        intent.getStringExtra(USER_PASSWORD));
            } catch (Exception e) {
                // to google analytics
            }

            executeGetResult = intent.getIntExtra(EXECUTE_MODEL, 0);

            new AsyncTaskRequest().execute();
        }

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

            jsonArrayString = null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DbConn = connectModel.getQueriesGenerator().getConnection();

                if(DbConn != null && connectModel.getUserRequestToServer() != null){
                    statement = DbConn.createStatement();
                    if(executeGetResult == 2) {
                        resultSet = statement.executeQuery(connectModel.getUserRequestToServer());
                        jsonArrayString = SqlJsonUtils.createJsonArrayFromResultSet(resultSet).toString();
                    }else{
                        statement.executeUpdate(connectModel.getUserRequestToServer());
                    }
                }else{
                    throw new Exception("connection filed");
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
            if(jsonArrayString != null){
                // send data from server
                try {
                    receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS,
                            ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS_SEND_DATA);
                    receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_DATA, jsonArrayString);
                } catch (Exception e) {
                    // for Google Analytics

                }
            }else{
                if(executeGetResult == 0) {
                    // send confirmation connection
                    receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS,
                            ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS_CONNECT);
                }else if(executeGetResult == 1){
                    // send confirmation query execute
                    receiveIntent.putExtra(ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS,
                            ServerRequestBroadcastReceiver.BROADCASTRECEIVER_STATUS_QUERY_EXEC);
                }
            }
        }
        sendBroadcast(receiveIntent);
    }
}
