package by.madcat.development.databaseviewer.SupportClasses;

public enum SqlTypes {
    INTEGER(0),
    BOOLEAN(1),
    DECIMAL(2),
    VARCHAR(3),
    DATATIME(4),
    BLOB(5);

    private int id;

    SqlTypes(int i) {
        id = i;
    }

    int getId(){
        return id;
    }
}
