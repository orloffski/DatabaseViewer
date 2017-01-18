package by.madcat.development.databaseviewer.Utils.QueriesGenerators;

import by.madcat.development.databaseviewer.Models.TableMetadataModel;

public interface QueriesGeneratorInterface {
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
}
