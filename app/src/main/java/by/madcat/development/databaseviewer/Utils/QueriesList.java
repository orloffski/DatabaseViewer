package by.madcat.development.databaseviewer.Utils;

public class QueriesList {
    // get all databases from sql server (without system db)
    public static final String DATABASES_LIST_QUERY = "SELECT name FROM master.dbo.sysdatabases WHERE dbid > 4";
    // create database
    public static final String DATABASE_ADD = "CREATE DATABASE ";
    // rename database
    public static final String DATABASE_EDIT = "ALTER DATABASE %s Modify Name = %s";
    // delete database
    public static final String DATABASE_DELETE = "DROP DATABASE ";
    // get all tables from selected database
    public static final String TABLES_LIST_QUERY = "Use %s SELECT Name FROM dbo.sysobjects WHERE (xtype = 'U')";
}
