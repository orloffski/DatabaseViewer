package by.madcat.development.databaseviewer.Utils;

import android.util.Log;

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

    public String getTableName() {
        return tableName;
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
            this.primaryKey = false;
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

        @Override
        public boolean equals(Object obj) {
            Fields otherFields = (Fields) obj;

            if(!this.fieldName.equals(otherFields.fieldName) ||
                    !this.getType().equals(otherFields.getType()) ||
                    this.getLength() != otherFields.getLength()) {
                return false;
            }

            if(this.isPrimaryKey() && otherFields.isPrimaryKey()) {
                Log.d("payment", "not equals");
                return false;
            }

            return true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        TableMetadataModel otherTable = (TableMetadataModel) obj;

        if(!this.tableName.equals(otherTable.tableName) ||
                this.fieldsList.size() != otherTable.fieldsList.size())
            return false;

        for(int i = 0; i < this.fieldsList.size(); i++){
            if(!this.fieldsList.get(i).equals(otherTable.fieldsList.get(i)))
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("table: ").append(this.tableName);
        stringBuilder.append(" fields count: ").append(this.fieldsList.size());
        stringBuilder.append(" fields list: ");

        for(int i = 0; i < this.fieldsList.size(); i++){
            stringBuilder.append(" fieldName: ").append(this.fieldsList.get(i).getFieldName());
            stringBuilder.append(" fieldType: ").append(this.fieldsList.get(i).getType());
            stringBuilder.append(" fieldLength: ").append(this.fieldsList.get(i).getLength());
            stringBuilder.append(" fieldPrimaryKey: ").append(this.fieldsList.get(i).isPrimaryKey());
        }

        return stringBuilder.toString();
    }
}
