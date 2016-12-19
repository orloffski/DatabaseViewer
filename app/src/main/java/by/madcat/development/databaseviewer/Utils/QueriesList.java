package by.madcat.development.databaseviewer.Utils;

public class QueriesList {
    public static final String DATABASES_LIST_QUERY = "SELECT name FROM master.dbo.sysdatabases WHERE dbid > 4";
    public static final String DATABASE_ADD = "CREATE DATABASE ";
    public static final String DATABASE_EDIT = "ALTER DATABASE %s Modify Name = %s";
    public static final String DATABASE_DELETE = "DROP DATABASE ";
}
