package by.madcat.development.databaseviewer.Models;

import java.util.Map;
import java.util.TreeMap;

public class PrimaryKeysModel {
    private static PrimaryKeysModel instance;

    private Map<String, KeysModel> keysData;

    private PrimaryKeysModel(){
        keysData = new TreeMap<>();
    }

    public static PrimaryKeysModel getInstance(){
        if(instance == null){
            instance = new PrimaryKeysModel();
        }

        return instance;
    }

    public void addKey(String tableName, String fieldName, int position){
        keysData.put(tableName, new KeysModel(fieldName, position));
    }

    public String getFieldByTableName(String tableName){
        KeysModel model = this.keysData.get(tableName);

        return model.getFieldName();
    }

    public int getPositionByTableName(String tableName){
        KeysModel model = this.keysData.get(tableName);

        return model.getPosition();
    }

    private class KeysModel{
        private String fieldName;
        private int position;

        public KeysModel(String fieldName, int position){
            this.fieldName = fieldName;
            this.position = position;
        }

        public String getFieldName() {
            return fieldName;
        }

        public int getPosition() {
            return position;
        }
    }
}
