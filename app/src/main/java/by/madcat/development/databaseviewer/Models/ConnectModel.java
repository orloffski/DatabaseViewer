package by.madcat.development.databaseviewer.Models;

import by.madcat.development.databaseviewer.Utils.QueriesGenerators.DatabasesTypes;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.QueriesGeneratorInterface;

public class ConnectModel {

    public static final String CONNECTION_STRING = "jdbc:jtds:sqlserver://";

    private String serverIpAdress;
    private String userName;
    private String userPassword;
    public String userRequestToServer;
    private QueriesGeneratorInterface queriesGenerator;

    private static ConnectModel instance;

    private ConnectModel(String serverIpAdress, QueriesGeneratorInterface queriesGenerator, String userName, String userPassword){
        this.serverIpAdress = serverIpAdress;
        this.userName = userName;
        this.userPassword = userPassword;
        this.queriesGenerator = queriesGenerator;
    }

    public static ConnectModel getInstance(String serverIpAdress, QueriesGeneratorInterface queriesGenerator, String userName, String userPassword){
        if(instance == null){

            instance = new ConnectModel(serverIpAdress, queriesGenerator, userName, userPassword);
        }

        return instance;
    }

    public static ConnectModel updateInstance(String serverIpAdress, QueriesGeneratorInterface queriesGenerator, String userName, String userPassword){
        if(instance == null){
            instance = new ConnectModel(serverIpAdress, queriesGenerator, userName, userPassword);
        }else{
            instance.serverIpAdress = serverIpAdress;
            instance.userName = userName;
            instance.userPassword = userPassword;
            instance.queriesGenerator = queriesGenerator;
        }

        return instance;
    }

    public String getUserRequestToServer() {
        return userRequestToServer;
    }

    public void setUserRequestToServer(String userRequestToServer) {
        this.userRequestToServer = userRequestToServer;
    }

    public String getServerIpAdress() {
        return serverIpAdress;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public QueriesGeneratorInterface getQueriesGenerator() {
        return queriesGenerator;
    }
}
