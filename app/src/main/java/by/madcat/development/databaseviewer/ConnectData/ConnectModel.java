package by.madcat.development.databaseviewer.ConnectData;

public class ConnectModel {

    public static final String CONNECTION_STRING = "jdbc:jtds:sqlserver://";

    private String serverIpAdress;
    private String userName;
    private String userPassword;
    public String userRequestToServer;

    private static ConnectModel instance;

    private ConnectModel(String serverIpAdress, String userName, String userPassword){
        this.serverIpAdress = serverIpAdress;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public static ConnectModel getInstance(String serverIpAdress, String userName, String userPassword){
        if(instance == null){

            instance = new ConnectModel(serverIpAdress, userName, userPassword);
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
}
