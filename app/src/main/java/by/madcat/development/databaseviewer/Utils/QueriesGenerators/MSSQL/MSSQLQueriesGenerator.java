package by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQL;

import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.QueriesGeneratorInterface;
import by.madcat.development.databaseviewer.Utils.SqlTypes;

public class MSSQLQueriesGenerator implements QueriesGeneratorInterface {

    @Override
    public String getPrimaryKeysList(String databaseName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(MSSQLQueriesPartsList.PRIMARY_KEYS_STRING_1, databaseName));
        stringBuilder.append(MSSQLQueriesPartsList.PRIMARY_KEYS_STRING_2);
        return stringBuilder.toString();
    }

    @Override
    public String getDatabasesList(){
        return MSSQLQueriesPartsList.DATABASES_LIST_QUERY;
    }

    @Override
    public String createDatabase(String databaseName){
        return (MSSQLQueriesPartsList.DATABASE_ADD + databaseName);
    }

    @Override
    public String renameDatabase(String databaseOldName, String databaseNewName){
        return String.format(MSSQLQueriesPartsList.DATABASE_EDIT, databaseOldName, databaseNewName);
    }

    @Override
    public String deleteDatabase(String databaseName) {
        return (MSSQLQueriesPartsList.DATABASE_DELETE + databaseName);
    }

    @Override
    public String getTablesList(String databaseName){
        return String.format(MSSQLQueriesPartsList.TABLES_LIST_QUERY, databaseName);
    }

    @Override
    public String getPrimaryKeyPart(String fieldName){
        return String.format(MSSQLQueriesPartsList.TABLE_EDIT_PRIMARY_KEY, fieldName);
    }

    @Override
    public String createTable(String databaseName, String tableName, String fieldsParts){
        return String.format(MSSQLQueriesPartsList.TABLE_ADD, databaseName, tableName, fieldsParts);
    }

    @Override
    public String deleteTable(String databaseName, String tableName) {
        return String.format(MSSQLQueriesPartsList.TABLE_DELETE,
                databaseName, tableName);
    }

    @Override
    public String getTableMetadata(String databaseName, String tableName){
        return String.format(MSSQLQueriesPartsList.TABLE_METADATA, databaseName, tableName);
    }

    @Override
    public String changeTable(TableMetadataModel oldTable, TableMetadataModel newTable, String databaseName, String tableName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MSSQLQueriesPartsList.BEGIN_TRANSACTION);
        stringBuilder.append(String.format(MSSQLQueriesPartsList.DB_SELECT, databaseName));

        if(!oldTable.getTableName().equals(newTable.getTableName()))
            stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_NAME, tableName, newTable.getTableName()));

        for(int i = 0; i < oldTable.getFieldsList().size(); i++){
            if(newTable.getFieldsList().get(i).isFieldToDelete()){
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT, newTable.getTableName()));
                stringBuilder.append(
                        String.format(MSSQLQueriesPartsList.TABLE_EDIT_DELETE_FIELD, oldTable.getFieldsList().get(i).getFieldName()));
            }else{
                if(!oldTable.getFieldsList().get(i).getFieldName().equals(newTable.getFieldsList().get(i).getFieldName())){
                    stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_CHANGE_FIELD_RENAME,
                            newTable.getTableName(),
                            oldTable.getFieldsList().get(i).getFieldName(),
                            newTable.getFieldsList().get(i).getFieldName()));
                }

                if(!oldTable.getFieldsList().get(i).getType().equals(newTable.getFieldsList().get(i).getType())){
                    String type = "";
                    if(newTable.getFieldsList().get(i).getType().equals(SqlTypes.VARCHAR)){
                        type = SqlTypes.VARCHAR.toString() + "(" + newTable.getFieldsList().get(i).getLength() + ")";
                    }else{
                        type = newTable.getFieldsList().get(i).getType().toString();
                    }

                    stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_CHANGE_FIELD_TYPE,
                            newTable.getTableName(),
                            newTable.getFieldsList().get(i).getFieldName(),
                            type));
                }

                if(oldTable.getFieldsList().get(i).getLength() != newTable.getFieldsList().get(i).getLength() && newTable.getFieldsList().get(i).getType().equals(SqlTypes.VARCHAR)){
                    String length = newTable.getFieldsList().get(i).getType().toString() +
                            "(" +
                            newTable.getFieldsList().get(i).getLength() +
                            ")";
                    stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_CHANGE_FIELD_TYPE,
                            newTable.getTableName(),
                            newTable.getFieldsList().get(i).getFieldName(),
                            length));
                }
            }
        }

        for(int i = oldTable.getFieldsList().size(); i < newTable.getFieldsList().size(); i++){
            if(newTable.getFieldsList().get(i).getLength() != 0){
                String length = newTable.getFieldsList().get(i).getType().toString() +
                        "(" +
                        newTable.getFieldsList().get(i).getLength() +
                        ")";
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_ADD_FIELD,
                        newTable.getFieldsList().get(i).getFieldName(),
                        newTable.getFieldsList().get(i).getType()));
                stringBuilder.append(length).append(";");
            }else{
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT, newTable.getTableName()));
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_ADD_FIELD,
                        newTable.getFieldsList().get(i).getFieldName(),
                        newTable.getFieldsList().get(i).getType())).append(";");
            }
        }

        stringBuilder.append(MSSQLQueriesPartsList.COMMIT_TRANSACTION);

        return stringBuilder.toString();
    }

    @Override
    public String getRecordsList(String databaseName, String tableName){
        return String.format(MSSQLQueriesPartsList.RECORDS_LIST_QUERY, databaseName, tableName);
    }

    @Override
    public String deleteRecord(String databaseName, String tableName, String primaryKeyFieldName, String primaryKey){
        return String.format(MSSQLQueriesPartsList.RECORD_DELETE, databaseName, tableName, primaryKeyFieldName, primaryKey);
    }

    @Override
    public String getRecord(String databaseName, String tableName, String primaryKeyFieldName, String primaryKey){
        return String.format(MSSQLQueriesPartsList.RECORDS, databaseName, tableName, primaryKeyFieldName, primaryKey);
    }

    @Override
    public String insertRecords(String databaseName, String tableName, String fieldsList){
        return String.format(MSSQLQueriesPartsList.RECORD_INSERT, databaseName, tableName, fieldsList);
    }

    @Override
    public String updateRecord(String databaseName, String tableName, String updateColumnsString, String updateRecordKeyString){
        return String.format(MSSQLQueriesPartsList.RECORD_UPDATE, databaseName, tableName, updateColumnsString, updateRecordKeyString);
    }

    @Override
    public String userQuery(String databaseName, String userQueryString){
        return String.format(MSSQLQueriesPartsList.QUERY, databaseName, userQueryString);
    }
}
