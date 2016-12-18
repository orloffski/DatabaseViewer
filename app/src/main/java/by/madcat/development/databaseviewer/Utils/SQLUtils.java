package by.madcat.development.databaseviewer.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtils {
    public static final JSONArray createJconArrayFromResultSet(ResultSet resultSet) throws SQLException, JSONException {
        JSONArray jsonArray = new JSONArray();

        if(resultSet != null){
            int columnCount = resultSet.getMetaData().getColumnCount();
            while(resultSet.next()){
                JSONObject rowObject = new JSONObject();
                for(int i = 1; i <= columnCount; i++)
                    rowObject.put(resultSet.getMetaData().getColumnName(i),
                            (resultSet.getString(i) != null) ? resultSet.getString(i) : "");
                jsonArray.put(rowObject);
            }
        }

        return jsonArray;
    }
}
