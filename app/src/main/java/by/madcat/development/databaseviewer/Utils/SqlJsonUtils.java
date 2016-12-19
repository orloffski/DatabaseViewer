package by.madcat.development.databaseviewer.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

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
}
