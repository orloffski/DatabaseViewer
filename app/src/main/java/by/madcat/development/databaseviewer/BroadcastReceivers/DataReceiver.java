package by.madcat.development.databaseviewer.BroadcastReceivers;

public interface DataReceiver {
    void sendErrorMessage(String errorMessage);
    void sendConnectConfirmation();
    void sendDataFromServer(String jsonArrayData);
    void sendQueryExecutedNoResult();
}
