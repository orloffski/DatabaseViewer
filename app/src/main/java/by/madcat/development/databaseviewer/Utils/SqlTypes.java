package by.madcat.development.databaseviewer.Utils;

public enum SqlTypes {
    SMALLINT("smallint"),
    INT("int"),
    REAL("real"),
    MONEY("money"),
    TINYINT("tinyint"),
    BIT("bit"),
    DECIMAL("decimal"),
    CHAR("char"),
    VARCHAR("varchar"),
    TEXT("text"),
    IMAGE("image"),
    DATE("date"),
    TIME("time"),
    DATETIME("datetime");

    private String id;

    SqlTypes(String i) {
        id = i;
    }

    String getId(){
        return id;
    }
}
