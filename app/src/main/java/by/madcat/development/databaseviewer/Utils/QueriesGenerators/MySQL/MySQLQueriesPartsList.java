package by.madcat.development.databaseviewer.Utils.QueriesGenerators.MySQL;

public class MySQLQueriesPartsList {
    public static final String DATABASES_LIST_QUERY = "SHOW DATABASES";
    public static final String DATABASE_LIST_KEY = "SCHEMA_NAME";
    public static final String DATABASE_ADD = "CREATE DATABASE ";
    public static final String DATABASE_DELETE = "DROP DATABASE ";
    public static final String TABLES_LIST_QUERY = "SHOW TABLES FROM %s";
    public static final String TABLE_LIST_KEY = "TABLE_NAME";
    public static final String PRIMARY_KEYS_STRING =
            "SELECT TABLE_NAME, COLUMN_NAME FROM information_schema.columns " +
                    "WHERE column_key = 'PRI' AND TABLE_SCHEMA = '%s'";
    public static final String TABLE_METADATA =
            "Use %s SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME = '%s'";
}
