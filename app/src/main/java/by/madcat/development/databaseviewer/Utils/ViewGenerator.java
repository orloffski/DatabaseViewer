package by.madcat.development.databaseviewer.Utils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.sql.Types;

public class ViewGenerator {
    public static View generateNewFieldView(Context context, Types type){
        View view = new LinearLayout(context);

        return view;
    }

    public static View generateIssetFieldView(Context context, Types type){
        View view = new LinearLayout(context);

        return view;
    }

    public static View updateFieldView(Context context, Types newType, View oldView){
        return oldView;
    }
}
