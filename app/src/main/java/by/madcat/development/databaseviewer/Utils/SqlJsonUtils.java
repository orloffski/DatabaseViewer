package by.madcat.development.databaseviewer.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import by.madcat.development.databaseviewer.Models.PrimaryKeysModel;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;

public class SqlJsonUtils {
    public static final JSONArray createJsonArrayFromResultSet(ResultSet resultSet){
        JSONArray jsonArray = new JSONArray();
        try {
            if(resultSet != null){
                int columnCount = 0;

                    columnCount = resultSet.getMetaData().getColumnCount();

                while(resultSet.next()){
                    JSONObject rowObject = new JSONObject();
                    for(int i = 1; i <= columnCount; i++)
                        rowObject.put(resultSet.getMetaData().getColumnName(i),
                                (resultSet.getString(i) != null) ? resultSet.getString(i) : "");
                    jsonArray.put(rowObject);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static final TableMetadataModel createTableMetadata(String jsonArrayData, String tableName, PrimaryKeysModel primaryKeysModel){
        TableMetadataModel tableMetadata = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayData);
            tableMetadata = new TableMetadataModel(tableName);

            for(int i = 0; i < jsonArray.length(); i++){
                String fieldName = jsonArray.getJSONObject(i).getString("COLUMN_NAME").toString();
                String type = jsonArray.getJSONObject(i).getString("DATA_TYPE").toString().toUpperCase();
                int length = jsonArray.getJSONObject(i).getString("CHARACTER_MAXIMUM_LENGTH").toString().equals("") ? 0 :
                        Integer.parseInt(jsonArray.getJSONObject(i).getString("CHARACTER_MAXIMUM_LENGTH").toString());

                boolean isKey = false;
                if(primaryKeysModel.getFieldName(tableName).equals(fieldName))
                    isKey = true;

                tableMetadata.addNewField(fieldName, type, length, isKey);
            }
        } catch (JSONException e                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ) {
            // for Google Analytics
        }

        return tableMetadata;
    }

    public static final ArrayList<String> getJsonKeys(JSONObject jsonObject){
        Iterator keysIterator = jsonObject.keys();
        ArrayList<String> keysList = new ArrayList<>();

        while(keysIterator.hasNext()){
            String key = (String) keysIterator.next();
            keysList.add(key);
        }

        return keysList;
    }

    public static final ArrayList<String> getJsonValues(JSONObject jsonObject, ArrayList<String> keys) throws JSONException {
        ArrayList<String> valuesList = new ArrayList<>();

        for(String key: keys)
            valuesList.add(jsonObject.getString(key));

        return valuesList;
    }
}
