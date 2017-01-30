package by.madcat.development.databaseviewer.Utils.QueriesGenerators.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.DatabasesTypes;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.QueriesGeneratorInterface;

public class MySQLQueriesGenerator implements QueriesGeneratorInterface {
    @Override
    public Connection getConnection() throws Exception {
        Connection MySQLConnect = null;
        ConnectModel connectModel = ConnectModel.getInstance("", null, "", "");

        Class.forName("com.mysql.jdbc.Driver");
        MySQLConnect = DriverManager.getConnection(ConnectModel.MYSQL_CONNECTION_STRING + connectModel.getServerIpAdress() + ":3306", connectModel.getUserName(), connectModel.getUserPassword());

        return MySQLConnect;
    }

    @Override
    public String getDatabaseType() {
        return DatabasesTypes.MySQL.toString();
    }

    @Override
    public String getDatabaseListKey() {
        return MySQLQueriesPartsList.DATABASE_LIST_KEY;
    }

    @Override
    public String getTableListKey() {
        return (MySQLQueriesPartsList.TABLE_LIST_KEY);
    }

    @Override
    public String getPrimaryKeysList(String databaseName) {
        return String.format(MySQLQueriesPartsList.PRIMARY_KEYS_STRING, databaseName);
    }

    @Override
    public String getDatabasesList() {
        return MySQLQueriesPartsList.DATABASES_LIST_QUERY;
    }

    @Override
    public String createDatabase(String databaseName) {
        return MySQLQueriesPartsList.DATABASE_ADD + databaseName;
    }

    @Override
    public String renameDatabase(String databaseOldName, String databaseNewName) {
        return null;
    }

    @Override
    public String deleteDatabase(String databaseName) {
        return MySQLQueriesPartsList.DATABASE_DELETE + databaseName;
    }

    @Override
    public String getTablesList(String databaseName) {
        return String.format(MySQLQueriesPartsList.TABLES_LIST_QUERY, databaseName);
    }

    @Override
    public String getPrimaryKeyPart(String fieldName) {
        return null;
    }

    @Override
    public String createTable(String databaseName, String tableName, String fieldsParts) {
        return null;
    }

    @Override
    public String deleteTable(String databaseName, String tableName) {
        return null;
    }

    @Override
    public String getTableMetadata(String databaseName, String tableName) {
        return String.format(MySQLQueriesPartsList.TABLE_METADATA, databaseName, tableName);
    }

    @Override
    public String changeTable(TableMetadataModel oldTable, TableMetadataModel newTable, String databaseName, String tableName) {
        return null;
    }

    @Override
    public String getRecordsList(String databaseName, String tableName) {
        return null;
    }

    @Override
    public String deleteRecord(String databaseName, String tableName, String primaryKeyFieldName, String primaryKey) {
        return null;
    }

    @Override
    public String getRecord(String databaseName, String tableName, String primaryKeyFieldName, String primaryKey) {
        return null;
    }

    @Override
    public String insertRecords(String databaseName, String tableName, String fieldsList) {
        return null;
    }

    @Override
    public String updateRecord(String databaseName, String tableName, String updateColumnsString, String updateRecordKeyString) {
        return null;
    }

    @Override
    public String userQuery(String databaseName, String userQueryString) {
        return null;
    }

    @Override
    public ArrayList<String> getNumberTypes() {
        ArrayList<String> numberTypes = new ArrayList<>();

        numberTypes.add(SqlTypes.TINYINT.toString());
        numberTypes.add(SqlTypes.SMALLINT.toString());
        numberTypes.add(SqlTypes.INT.toString());
        numberTypes.add(SqlTypes.BIGINT.toString());
        numberTypes.add(SqlTypes.REAL.toString());

        return numberTypes;
    }

    @Override
    public ArrayList<String> getDateTimeTypes() {
        ArrayList<String> dateTimeTypes = new ArrayList<>();

        dateTimeTypes.add(SqlTypes.DATE.toString());
        dateTimeTypes.add(SqlTypes.TIME.toString());
        dateTimeTypes.add(SqlTypes.DATETIME.toString());
        dateTimeTypes.add(SqlTypes.TIMESTAMP.toString());

        return dateTimeTypes;
    }
}
