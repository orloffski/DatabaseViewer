package by.madcat.development.databaseviewer.Models;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQL.SqlTypes;

public class TableMetadataModel implements Cloneable{
    private String tableName;
    private ArrayList<Fields> fieldsList;

    public TableMetadataModel(String name){
        this.tableName = name;
        this.fieldsList = new ArrayList<>();
    }

    public Fields addNewField(String fieldName, SqlTypes fieldType, int length, boolean isKey){
        Fields newField = new Fields(fieldName, fieldType, length, isKey);
        this.fieldsList.add(newField);
        return newField;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ArrayList<Fields> getFieldsList() {
        return fieldsList;
    }

    public final class Fields{
        private String fieldName;
        private SqlTypes type;
        private int length;
        private boolean primaryKey;
        private boolean fieldToDelete;

        public Fields(String name, SqlTypes type, int length, boolean isKey){
            this.fieldName = name;
            this.type = type;
            this.length = length;
            this.primaryKey = isKey;
            this.fieldToDelete = false;
        }

        private void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        private void setType(SqlTypes type) {
            this.type = type;
        }

        private void setLength(int length) {
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

        public boolean isFieldToDelete() {
            return fieldToDelete;
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
                return false;
            }

            return true;
        }
    }

    public void setFieldToDeleteByName(String fieldName){
        Fields field = getFieldByName(fieldName);

        if(field != null)
            field.fieldToDelete = true;
    }

    public void updateField(String name, SqlTypes type, int length){
        Fields field = getFieldByName(name);

        if(field != null){
            field.setFieldName(name);
            field.setType(type);
            field.setLength(length);
        }
    }

    public Fields getFieldByName(String fieldName){
        Fields field = null;

        for(int i = 0; i < this.getFieldsList().size(); i++){
            if(this.getFieldsList().get(i).getFieldName().equals(fieldName))
                field = this.getFieldsList().get(i);
        }

        if(field != null)
            return field;

        return null;
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
    public TableMetadataModel clone() {
        TableMetadataModel newTableMetadata;

        newTableMetadata = new TableMetadataModel(this.getTableName());

        for(int i = 0; i < this.getFieldsList().size(); i++){
            Fields field = this.getFieldsList().get(i);

            newTableMetadata.addNewField(field.getFieldName(), field.getType(), field.getLength(), field.isPrimaryKey());
        }

        return newTableMetadata;
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
            stringBuilder.append(" fieldToDelete: ").append(this.fieldsList.get(i).isFieldToDelete());
        }

        return stringBuilder.toString();
    }
}
