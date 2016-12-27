package by.madcat.development.databaseviewer.Utils;

public class QueriesList {
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
    public static final String TABLE_DELETE = "Use %s DROP TABLE %s";
    // create table (table name 2%s in db name 1%s where fields string 3%s)
    public static final String TABLE_ADD = "Use %s CREATE TABLE %s (%s)";
    // edit table
    public static final String TABLE_EDIT = "Use %s ALTER TABLE %s";
    public static final String TABLE_EDIT_NAME = " RENAME %s"; // rename table to newName %s
    public static final String TABLE_EDIT_ADD_FIELD = " ADD %s %s"; // add field name 1%s, type 2%s
    public static final String TABLE_EDIT_CHANGE_FIELD  = " CHANGE %s %s %s"; // change field name 1%s, to newName 2%s, to newType 3%s
    public static final String TABLE_EDIT_DELETE_FIELD = " DROP %s"; // dlete field name 1%s
    public static final String TABLE_EDIT_PRIMARY_KEY = "PRIMARY KEY (%s)"; // set primary key at 1%s field

    // transaction
    public static final String BEGIN_TRANSACTION = "BEGIN TRANSACTION";
    public static final String COMMIT_TRANSACTION = "COMMIT TRANSACTION";
}
