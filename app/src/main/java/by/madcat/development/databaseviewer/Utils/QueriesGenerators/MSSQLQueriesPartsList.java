package by.madcat.development.databaseviewer.Utils.QueriesGenerators;

public class MSSQLQueriesPartsList {
    // get all databases from sql server (without system db)
    public static final String DATABASES_LIST_QUERY = "SELECT name FROM master.dbo.sysdatabases WHERE dbid > 4";
    // create database
    public static final String DATABASE_ADD = "CREATE DATABASE ";
    // rename database (from name 1%s to 2%s)
    public static final String DATABASE_EDIT = "ALTER DATABASE %s Modify Name = %s";
    // delete database
    public static final String DATABASE_DELETE = "DROP DATABASE ";
    // get all tables from selected database (db name 1%s)
    public static final String TABLES_LIST_QUERY = "Use %s SELECT Name FROM dbo.sysobjects WHERE (xtype = 'U')";
    // delete table (table name 2%s from db name 1%s)
    public static final String TABLE_DELETE = "Use %s DROP TABLE %s ";
    // create table (table name 2%s in db name 1%s where fields string 3%s)
    public static final String TABLE_ADD = "Use %s CREATE TABLE %s (%s)";
    // get table metadata (get metadata from table 2%s in database 1%s, JOIN to get primary key in table)
    public static final String TABLE_METADATA =
            "Use %s SELECT data_table.COLUMN_NAME, data_table.DATA_TYPE, data_table.CHARACTER_MAXIMUM_LENGTH, keys_table.ORDINAL_POSITION " +
            "FROM INFORMATION_SCHEMA.COLUMNS AS data_table " +
            "LEFT OUTER JOIN information_schema.KEY_COLUMN_USAGE AS keys_table ON data_table.TABLE_NAME = keys_table.TABLE_NAME AND data_table.COLUMN_NAME = keys_table.COLUMN_NAME " +
            "where data_table.table_name = '%s'";
    // edit table
    public static final String DB_SELECT = "Use %s;";
    public static final String TABLE_EDIT = "ALTER TABLE %s ";
    public static final String TABLE_EDIT_NAME = "EXEC sp_rename %s, %s;"; // rename table 1%s to newName 2%s
    public static final String TABLE_EDIT_ADD_FIELD = "ADD %s %s;;"; // add field name 1%s, type 2%s
    public static final String TABLE_EDIT_CHANGE_FIELD_RENAME = "EXEC sp_RENAME '%s.%s', %s, 'COLUMN';";
    public static final String TABLE_EDIT_CHANGE_FIELD_TYPE = "ALTER TABLE %s ALTER COLUMN %s %s;";
    public static final String TABLE_EDIT_DELETE_FIELD = "DROP COLUMN %s;" ; // delete field name 1%s
    public static final String TABLE_EDIT_PRIMARY_KEY = "PRIMARY KEY (%s)"; // set primary key at 1%s field

    // transaction
    public static final String BEGIN_TRANSACTION = "BEGIN TRANSACTION;";
    public static final String COMMIT_TRANSACTION = "COMMIT;";
}
