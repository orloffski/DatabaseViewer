package by.madcat.development.databaseviewer.Utils.QueriesGenerators;

import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.Utils.SqlTypes;

public class MSSQLQueriesGenerator {
    public static final String getPrimaryKeysList(String databaseName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(MSSQLQueriesPartsList.PRIMARY_KEYS_STRING_1, databaseName));
        stringBuilder.append(MSSQLQueriesPartsList.PRIMARY_KEYS_STRING_2);
        return stringBuilder.toString();
    }

    public static final String getDatabasesList(){
        return MSSQLQueriesPartsList.DATABASES_LIST_QUERY;
    }

    public static final String createDatabase(String databaseName){
        return (MSSQLQueriesPartsList.DATABASE_ADD + databaseName);
    }

    public static final String renameDatabase(String databaseOldName, String databaseNewName){
        return String.format(MSSQLQueriesPartsList.DATABASE_EDIT, databaseOldName, databaseNewName);
    }

    public static final String getTablesList(String databaseName){
        return String.format(MSSQLQueriesPartsList.TABLES_LIST_QUERY, databaseName);
    }

    public static final String getPrimaryKeyPart(String fieldName){
        return String.format(MSSQLQueriesPartsList.TABLE_EDIT_PRIMARY_KEY, fieldName);
    }

    public static final String createTable(String databaseName, String tableName, String fieldsParts){
        return String.format(MSSQLQueriesPartsList.TABLE_ADD, databaseName, tableName, fieldsParts);
    }

    public static final String getTableMetadata(String databaseName, String tableName){
        return String.format(MSSQLQueriesPartsList.TABLE_METADATA, databaseName, tableName);
    }

    public static final String changeTable(TableMetadataModel oldTable, TableMetadataModel newTable, String databaseName, String tableName){
        int counter;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MSSQLQueriesPartsList.BEGIN_TRANSACTION);
        stringBuilder.append(String.format(MSSQLQueriesPartsList.DB_SELECT, databaseName));

        if(!oldTable.getTableName().equals(newTable.getTableName()))
            stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_NAME, tableName, newTable.getTableName()));

        counter = oldTable.getFieldsList().size();

        if(counter > newTable.getFieldsList().size()) {
            counter = newTable.getFieldsList().size();
        }

        for(int i = 0; i < counter; i++){
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

        if(newTable.getFieldsList().size() < oldTable.getFieldsList().size()){
            for(int i = counter; i < oldTable.getFieldsList().size(); i++){
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT, newTable.getTableName()));
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_DELETE_FIELD, oldTable.getFieldsList().get(i).getFieldName()));
            }
        }else{
            for(int i = counter; i < newTable.getFieldsList().size(); i++){
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT, newTable.getTableName()));
                stringBuilder.append(String.format(MSSQLQueriesPartsList.TABLE_EDIT_ADD_FIELD,
                        newTable.getFieldsList().get(i).getFieldName(),
                        newTable.getFieldsList().get(i).getType()));
            }
        }

        stringBuilder.append(MSSQLQueriesPartsList.COMMIT_TRANSACTION);

        return stringBuilder.toString();
    }

    public static final String getRecordsList(String databaseName, String tableName){
        return String.format(MSSQLQueriesPartsList.RECORDS_LIST_QUERY, databaseName, tableName);
    }

    public static final String deleteRecord(String databaseName, String tableName, String primaryKeyFieldName, String primaryKey){
        return String.format(MSSQLQueriesPartsList.RECORD_DELETE, databaseName, tableName, primaryKeyFieldName, primaryKey);
    }

    public static final String getRecord(String databaseName, String tableName, String primaryKeyFieldName, String primaryKey){
        return String.format(MSSQLQueriesPartsList.RECORD, databaseName, tableName, primaryKeyFieldName, primaryKey);
    }
}
