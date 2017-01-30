package by.madcat.development.databaseviewer.Utils.QueriesGenerators.MySQL;

public enum SqlTypes {
    // numbers
    TINYINT("tinyint"),
    SMALLINT("smallint"),
    INT("int"),
    BIGINT("bigint"),
    REAL("real"),

    // text
    TINYTEXT("tinytext"),
    TEXT("text"),
    MEDIUMTEXT("mediumtext"),

    // date-time
    DATE("date"),
    TIME("time"),
    DATETIME("datetime"),
    TIMESTAMP("timestamp");

    private String id;

    SqlTypes(String i) {
        id = i;
    }

    String getId(){
        return id;
    }
}
