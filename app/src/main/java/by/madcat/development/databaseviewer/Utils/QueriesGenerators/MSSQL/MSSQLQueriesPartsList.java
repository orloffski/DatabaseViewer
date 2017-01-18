package by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQL;

public class MSSQLQueriesPartsList {
    // get primary keys
    public static final String PRIMARY_KEYS_STRING_1 =
            "USE %s SELECT TABLE_NAME, COLUMN_NAME FROM information_schema.KEY_COLUMN_USAGE ";
    public static final String PRIMARY_KEYS_STRING_2 = "WHERE CONSTRAINT_NAME LIKE 'PK%'";
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
    // get table metadata (get metadata from table 2%s in database 1%s)
    public static final String TABLE_METADATA =
            "Use %s SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME = '%s'";
    // edit table
    public static final String DB_SELECT = "Use %s;";
    public static final String TABLE_EDIT = "ALTER TABLE %s ";
    public static final String TABLE_EDIT_NAME = "EXEC sp_rename %s, %s;"; // rename table 1%s to newName 2%s
    public static final String TABLE_EDIT_ADD_FIELD = "ADD %s %s"; // add field name 1%s, type 2%s
    public static final String TABLE_EDIT_CHANGE_FIELD_RENAME = "EXEC sp_RENAME '%s.%s', %s, 'COLUMN';";
    public static final String TABLE_EDIT_CHANGE_FIELD_TYPE = "ALTER TABLE %s ALTER COLUMN %s %s;";
    public static final String TABLE_EDIT_DELETE_FIELD = "DROP COLUMN %s;" ; // delete field name 1%s
    public static final String TABLE_EDIT_PRIMARY_KEY = "PRIMARY KEY (%s)"; // set primary key at 1%s field

    // transaction
    public static final String BEGIN_TRANSACTION = "BEGIN TRANSACTION;";
    public static final String COMMIT_TRANSACTION = "COMMIT;";

    // get all records from 2%s table 1%s database
    public static final String RECORDS_LIST_QUERY = "Use %s SELECT * FROM %s";
    // delete record from table 2%s in database 1%s where id field name 3%s = 4%s
    public static final String RECORD_DELETE = "Use %s DELETE FROM %s WHERE %s = '%s'";
    // get record from 2%s table, 1%s database, where id field name 3%s = 4%s
    public static final String RECORDS = "Use %s SELECT * FROM %s WHERE %s = %s";
    // insert new record in table 2%s database 1%s fields string 3%s
    public static final String RECORD_INSERT = "Use %s INSERT INTO %s VALUES (%s)";
    // update record in table 2%s database 1%s set values 3%s where (id field name = key) 4%s
    public static final String RECORD_UPDATE = "Use %s UPDATE %s SET %s WHERE %s";

    // query from user, query 2%s on database 1%s
    public static final String QUERY = "Use %s %s";
}
