package by.madcat.development.databaseviewer.Utils.QueriesGenerators;

import java.sql.Connection;
import java.util.ArrayList;

import by.madcat.development.databaseviewer.Models.TableMetadataModel;

public interface QueriesGeneratorInterface {
    // get connection type
    Connection getConnection() throws Exception;

    // get DB type
    String getDatabaseType();

    // sql queries strings
    String getDatabaseListKey();
    String getTableListKey();
    String getPrimaryKeysList(String databaseName);
    String getDatabasesList();
    String createDatabase(String databaseName);
    String renameDatabase(String databaseOldName, String databaseNewName);
    String deleteDatabase(String databaseName);
    String getTablesList(String databaseName);
    String getPrimaryKeyPart(String fieldName);
    String createTable(String databaseName, String tableName, String fieldsParts);
    String deleteTable(String databaseName, String tableName);
    String getTableMetadata(String databaseName, String tableName);
    String changeTable(TableMetadataModel oldTable, TableMetadataModel newTable, String databaseName, String tableName);
    String getRecordsList(String databaseName, String tableName);
    String deleteRecord(String databaseName, String tableName, String primaryKeyFieldName, String primaryKey);
    String getRecord(String databaseName, String tableName, String primaryKeyFieldName, String primaryKey);
    String insertRecords(String databaseName, String tableName, String fieldsList);
    String updateRecord(String databaseName, String tableName, String updateColumnsString, String updateRecordKeyString);
    String userQuery(String databaseName, String userQueryString);

    // SqlTypes methods
    String[] getTypes();
    String[] getTypesValues();
    ArrayList<String> getNumberTypes();
    ArrayList<String> getDateTimeTypes();
    ArrayList<String> getTypesHavingLength();
    ArrayList<String> getBooleanTypes();
    ArrayList<String> getLoadedTypes();
}
