package by.madcat.development.databaseviewer.Utils;

import java.util.ArrayList;

public class TableMetadataModel {
    private String tableName;
    private ArrayList<Fields> fieldsList;

    public TableMetadataModel(String name){
        this.tableName = name;
        this.fieldsList = new ArrayList<>();
    }

    public Fields addNewField(String fieldName, SqlTypes fieldType, int length){
        Fields newField = new Fields(fieldName, fieldType, length);
        this.fieldsList.add(newField);
        return newField;
    }

    public ArrayList<Fields> getFieldsList() {
        return fieldsList;
    }

    public final class Fields{
        private String fieldName;
        private SqlTypes type;
        private int length;
        private boolean primaryKey;

        public Fields(String name, SqlTypes type, int length){
            this.fieldName = name;
            this.type = type;
            this.length = length;
        }

        public String getFieldName() {
            return fieldName;
        }

        public SqlTypes getType() {
            return type;
        }

        public int getLength() {
            return length;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }
    }
}
