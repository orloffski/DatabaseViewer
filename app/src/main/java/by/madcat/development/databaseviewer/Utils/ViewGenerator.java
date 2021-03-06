package by.madcat.development.databaseviewer.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import by.madcat.development.databaseviewer.ActivitiesUI.AddEditFieldView;
import by.madcat.development.databaseviewer.ActivitiesUI.AddEditFieldView.FieldDeleted;
import by.madcat.development.databaseviewer.ActivitiesUI.AddEditRecordActivity;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Models.PrimaryKeysModel;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.QueriesGeneratorInterface;

public class ViewGenerator {
    static int primaryKeyNumber = -1;

    public static final void addNewFieldInMainView(final Context context, final LinearLayout mainView, FieldDeleted fieldDeleted){
        final int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(matchParent, wrapContent);

        mainView.addView(generateNewFieldView(context, mainView, fieldDeleted), lParams);
    }

    public static final View generateNewFieldView(final Context context, final LinearLayout mainView, FieldDeleted fieldDeleted){
        return new AddEditFieldView(context, mainView, fieldDeleted);
    }

    public static final void addIssetFieldsInMainView(final Context context, final LinearLayout mainView, ArrayList<TableMetadataModel.Fields> fieldsArrayList, FieldDeleted fieldDeleted){
        ArrayList<View> views = generateIssetFieldsView(context, mainView, fieldsArrayList, fieldDeleted);

        for(View view : views)
            mainView.addView(view);
    }

    public static final ArrayList<View> generateIssetFieldsView(final Context context, final LinearLayout mainView, ArrayList<TableMetadataModel.Fields> fieldsArrayList, FieldDeleted fieldDeleted){
        ArrayList<View> views = new ArrayList<>();

        for(TableMetadataModel.Fields field : fieldsArrayList){
            View view = generateNewFieldView(context, mainView, fieldDeleted);

            fillValuesInFields(view, field);

            views.add(view);
        }

        return views;
    }

    private static final void fillValuesInFields(View view, TableMetadataModel.Fields field){
        QueriesGeneratorInterface queriesGeneratorInterface = ConnectModel.getInstance("", null, "", "").getQueriesGenerator();
        String[] types = queriesGeneratorInterface.getTypes();
        int position = Arrays.binarySearch(types, field.getType());

        EditText editText = (EditText) view.findViewById(R.id.fieldName);
        editText.setText(field.getFieldName());

        EditText length = (EditText) view.findViewById(R.id.fieldLenght);
        if(queriesGeneratorInterface.getTypesHavingLength().contains(types[position])){
            length.setText(String.valueOf(field.getLength()));
        }else{
            length.setHint("");
        }

        Spinner spinnerTypes = (Spinner) view.findViewById(R.id.typesSpinner);
        spinnerTypes.setSelection(position);

        CheckBox primaryKey = (CheckBox) view.findViewById(R.id.primaryKeyChb);
        primaryKey.setChecked(field.isPrimaryKey());
    }

    public static final void createViewRecordLine(final Context context, TableRow mainLayout, String[] data, boolean isHeader, int position, final String databaseName, final String tableName){
        final String primaryKeyFieldName;
        String primaryKey = "";

        PrimaryKeysModel primaryKeysModel = PrimaryKeysModel.getInstance();
        primaryKeyFieldName = primaryKeysModel.getFieldName(tableName);

        for(int i = 0; i < mainLayout.getChildCount(); i++)
            if(mainLayout.getChildAt(i) instanceof TextView)
                mainLayout.removeView(mainLayout.getChildAt(i));

        for(int i = 0; i < data.length; i++){
            ViewGroup.LayoutParams textViewParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.RIGHT);
            textView.setText(data[i]);
            textView.setPadding(10, 0, 0, 0);
            if(isHeader) {
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                if(primaryKeyFieldName.equals(data[i])) {
                    primaryKeyNumber = i;
                }
            }else{
                if(primaryKeyNumber != -1) {
                    primaryKey = data[primaryKeyNumber];
                }
            }

            mainLayout.addView(textView, i, textViewParams);
        }

        final String primaryKeyValue = primaryKey;
        ImageButton deleteRecordButton = (ImageButton)mainLayout.findViewById(R.id.deleteRecordButton);
        deleteRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectModel connectModel = ConnectModel.getInstance("", null, "", "");
                connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().deleteRecord(databaseName, tableName, primaryKeyFieldName, primaryKeyValue));

                Intent intent = new Intent(context, RequestService.class);
                intent.putExtra(RequestService.EXECUTE_MODEL, 1);
                context.startService(intent);
            }
        });

        ImageButton editRecordButton = (ImageButton)mainLayout.findViewById(R.id.editRecordButton);
        editRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddEditRecordActivity.getIntent(
                        context,
                        AddEditRecordActivity.RECORD_EDIT,
                        tableName,
                        databaseName,
                        primaryKeyValue,
                        primaryKeyFieldName);
                context.startActivity(intent);
            }
        });

        if(isHeader){
            mainLayout.removeView(deleteRecordButton);
            mainLayout.removeView(editRecordButton);
            mainLayout.addView(new TextView(context), new TableRow.LayoutParams(60, ViewGroup.LayoutParams.WRAP_CONTENT));
            mainLayout.addView(new TextView(context), new TableRow.LayoutParams(60, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        if(position == 0)
            mainLayout.setBackgroundColor(Color.GRAY);
        else if(position != 0 && position%2 == 0)
            mainLayout.setBackgroundColor(Color.rgb(233,233,233));
    }

    public static final void createRecordView(final Context context, TableLayout recordLayout, TableMetadataModel tableMetadata){
        int counter = 0;
        QueriesGeneratorInterface queriesGeneratorInterface = ConnectModel.getInstance("", null, "", "").getQueriesGenerator();

        for(TableMetadataModel.Fields field : tableMetadata.getFieldsList()){
            TableRow row = new TableRow(context);

            TextView fieldView = new TextView(context);
            fieldView.setText(field.getFieldName());
            fieldView.setTextColor(Color.BLACK);
            fieldView.setTypeface(Typeface.DEFAULT_BOLD);
            fieldView.setMinWidth(150);
            row.addView(fieldView);

            if(!queriesGeneratorInterface.getBooleanTypes().contains(field.getType())){
                EditText fieldValue = new EditText(context);
                fieldValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                fieldValue.setMaxLines(10);

                if (field.getLength() != 0)
                    fieldValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(field.getLength())});

                fieldValue.setInputType(getInputTypeBySqlType(field.getType()));
                fieldValue.setMinWidth(400);
                row.addView(fieldValue);
            }else{
                CheckBox fieldValue = new CheckBox(context);
                row.addView(fieldValue);
            }

            if(queriesGeneratorInterface.getLoadedTypes().contains(field.getType())){
                Button loadFilebutton = new Button(context);
                loadFilebutton.setText("choose file");
                loadFilebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                row.addView(loadFilebutton);
            }

            if(counter%2 ==0)
                row.setBackgroundColor(Color.rgb(233,233,233));

            recordLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            counter++;
        }
    }

    public static final void fillRecordView(TableLayout recordLayout, TableMetadataModel tableMetadata, JSONArray jsonArray){
        int count = recordLayout.getChildCount();

        for(int i = 0; i < count; i++){
            TableRow row = (TableRow) recordLayout.getChildAt(i);
            String fieldName = tableMetadata.getFieldsList().get(i).getFieldName();

            if(row.getChildAt(1) instanceof EditText) {
                EditText fieldValue = (EditText) row.getChildAt(1);

                try {
                    fieldValue.setText(jsonArray.getJSONObject(0).getString(fieldName));
                } catch (JSONException e) {
                    // for Google Analytics
                }
            }else if(row.getChildAt(1) instanceof CheckBox){
                CheckBox fieldValue = (CheckBox) row.getChildAt(1);
                int value = 0;

                try {
                    value = Integer.parseInt(jsonArray.getJSONObject(0).getString(fieldName));
                } catch (JSONException e) {
                    // for Google Analytics
                }

                fieldValue.setChecked(value == 1);
            }
        }
    }

    public static final int getInputTypeBySqlType(String type){
        QueriesGeneratorInterface queriesGeneratorInterface = ConnectModel.getInstance("", null, "", "").getQueriesGenerator();

        if(queriesGeneratorInterface.getNumberTypes().contains(type)){
            return (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        }
        if(queriesGeneratorInterface.getDateTimeTypes().contains(type)){
            return InputType.TYPE_CLASS_DATETIME;
        }

        return InputType.TYPE_CLASS_TEXT;
    }

    public static final void createResultsetRows(Context context, TableLayout recordLayout, ArrayList<ArrayList<String>> data){
        int counter = 0;

        for(int i = 0; i < data.size(); i++){
            ArrayList<String> dataLine= data.get(i);

            TableRow row = new TableRow(context);

            for(String lineValue: dataLine){
                TextView fieldView = new TextView(context);
                fieldView.setText(lineValue);
                fieldView.setTextColor(Color.BLACK);

                if(i == 0){
                    fieldView.setTypeface(Typeface.DEFAULT_BOLD);
                    fieldView.setBackgroundColor(Color.GRAY);
                }

                fieldView.setPadding(0, 0, 20, 0);

                row.addView(fieldView, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            }

            if(counter%2 == 0)
                row.setBackgroundColor(Color.rgb(233,233,233));

            recordLayout.addView(row,
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            counter++;
        }
    }
}
