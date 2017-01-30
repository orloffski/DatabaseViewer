package by.madcat.development.databaseviewer.Utils;

public enum SqlTypes {
    // data
    MONEY("money"),

    // numbers
    TINYINT("tinyint"),
    SMALLINT("smallint"),
    INT("int"),
    BIGINT("bigint"),
    REAL("real"),

    BIT("bit"),
    FLOAT("float"),
    DECIMAL("decimal"),

    // text
    CHAR("char"),
    VARCHAR("varchar"),
    TEXT("text"),
    IMAGE("image"),

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
