package by.madcat.development.databaseviewer.Utils;

public class QueriesList {
    public static final String DATABASES_LIST_QUERY = "SELECT name FROM master.dbo.sysdatabases WHERE dbid > 4";
}
