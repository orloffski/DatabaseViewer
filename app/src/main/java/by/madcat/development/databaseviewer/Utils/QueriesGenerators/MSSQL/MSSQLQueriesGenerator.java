package by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Properties;

import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.DatabasesTypes;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.QueriesGeneratorInterface;

public class MSSQLQueriesGenerator implements QueriesGeneratorInterface {

    @Override
    public Connection getConnection() throws Exception {
        Connection MSSQLConnect;
        ConnectModel connectModel = ConnectModel.getInstance("", null, "", "");

        Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        MSSQLConnect = DriverManager.getConnection(ConnectModel.MSSQL_CONNECTION_STRING + connectModel.getServerIpAdress() + ";", getUsersProperties());

        return MSSQLConnect;
    }

    @Override
    public String getDatabaseType() {
        return DatabasesTypes.MSSQL.toString();
    }

    @Override
    public String getDatabaseListKey() {
        return MSSQLQueriesPartsList.DATABASE_LIST_KEY;
    }

    @Override
    public String getTableListKey() {
        return MSSQLQueriesPartsList.TABLE_LIST_KEY;
    }

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

    @Override
    public String[] getTypes() {
        String[] types = new String[SqlTypes.values().length];
        SqlTypes[] sqlValues = SqlTypes.values();

        for(int i = 0; i < types.length; i++){
            types[i] = sqlValues[i].toString();
        }

        return types;
    }

    @Override
    public String[] getTypesValues() {
        String[] values = new String[SqlTypes.values().length];
        SqlTypes[] sqlValues = SqlTypes.values();

        for(int i = 0; i < values.length; i++){
            values[i] = sqlValues[i].toString();
        }

        return values;
    }

    @Override
    public ArrayList<String> getNumberTypes() {
        ArrayList<String> numberTypes = new ArrayList<>();

        numberTypes.add(SqlTypes.TINYINT.toString());
        numberTypes.add(SqlTypes.SMALLINT.toString());
        numberTypes.add(SqlTypes.INT.toString());
        numberTypes.add(SqlTypes.BIGINT.toString());
        numberTypes.add(SqlTypes.REAL.toString());
        numberTypes.add(SqlTypes.FLOAT.toString());
        numberTypes.add(SqlTypes.DECIMAL.toString());

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

    @Override
    public ArrayList<String> getTypesHavingLength() {
        ArrayList<String> typesHavingLength = new ArrayList<>();

        typesHavingLength.add(SqlTypes.TINYINT.toString());
        typesHavingLength.add(SqlTypes.SMALLINT.toString());
        typesHavingLength.add(SqlTypes.INT.toString());
        typesHavingLength.add(SqlTypes.BIGINT.toString());
        typesHavingLength.add(SqlTypes.FLOAT.toString());
        typesHavingLength.add(SqlTypes.DECIMAL.toString());
        typesHavingLength.add(SqlTypes.CHAR.toString());
        typesHavingLength.add(SqlTypes.VARCHAR.toString());

        return typesHavingLength;
    }

    @Override
    public ArrayList<String> getBooleanTypes() {
        ArrayList<String> booleanTypes = new ArrayList<>();

        booleanTypes.add(SqlTypes.BIT.toString());

        return booleanTypes;
    }

    @Override
    public ArrayList<String> getLoadedTypes() {
        ArrayList<String> loadedTypes = new ArrayList<>();

        loadedTypes.add(SqlTypes.IMAGE.toString());

        return loadedTypes;
    }

    private Properties getUsersProperties(){
        Properties props = new Properties();
        ConnectModel connectModel = ConnectModel.getInstance("", null, "", "");
        props.setProperty("user", connectModel.getUserName());
        props.setProperty("password", connectModel.getUserPassword());
        props.setProperty("socketTimeout", "2000");

        return props;
    }
}
