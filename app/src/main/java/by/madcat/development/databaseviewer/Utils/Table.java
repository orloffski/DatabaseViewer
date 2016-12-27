package by.madcat.development.databaseviewer.Utils;

import java.sql.Types;
import java.util.ArrayList;

public class Table {
    private String tableName;
    private ArrayList<Fields> fieldsList;
    boolean isPrimaryKey;

    public Table(String name){
        this.tableName = name;
        this.fieldsList = new ArrayList<>();
    }

    public Fields addNewField(String fieldName, Types fieldType){
        Fields newField = new Fields(fieldName, fieldType);
        this.fieldsList.add(newField);
        return newField;
    }

    private class Fields{
        private String fieldName;
        private Types type;
        private boolean primaryKey;

        public Fields(String name, Types type){
            this.fieldName = name;
            this.type = type;
        }
    }
}
