package by.madcat.development.databaseviewer.Models;

import java.util.Map;
import java.util.TreeMap;

public class PrimaryKeysModel {
    private static PrimaryKeysModel instance;

    private Map<String, String> keysData;

    private PrimaryKeysModel(){
        keysData = new TreeMap<>();
    }

    public static PrimaryKeysModel getInstance(){
        if(instance == null){
            instance = new PrimaryKeysModel();
        }

        return instance;
    }

    public void addKey(String tableName, String fieldName){
        keysData.put(tableName, fieldName);
    }

    public String getFieldName(String tableName){
        if(keysData.get(tableName) != null)
            return keysData.get(tableName);

        return "";
    }

    public void clearKeys(){
        this.keysData.clear();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for(Map.Entry<String, String> value : this.keysData.entrySet()){
            stringBuilder.append("table: ").append(value.getKey());
            stringBuilder.append(" field: ").append(value.getValue()).append("\n");
        }

        return stringBuilder.toString();
    }
}
