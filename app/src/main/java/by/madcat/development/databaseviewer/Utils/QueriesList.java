package by.madcat.development.databaseviewer.Utils;

public class QueriesList {
    public static final String DATABASES_LIST_QUERY = "SELECT name FROM master.dbo.sysdatabases WHERE dbid > 4";
    public static final String DATABASE_ADD = "CREATE DATABASE ";
    public static final String DATABASE_EDIT = "ALTER DATABASE $1 Modify Name = $2";
}
