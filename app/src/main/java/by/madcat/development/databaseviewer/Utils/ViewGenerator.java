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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import by.madcat.development.databaseviewer.AddEditRecordActivity;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.Models.PrimaryKeysModel;
import by.madcat.development.databaseviewer.Models.TableMetadataModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQLQueriesGenerator;

public class ViewGenerator {
    static int primaryKeyNumber = -1;

    public static final void addNewFieldInMainView(final Context context, final LinearLayout mainView){
        final int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(matchParent, wrapContent);

        mainView.addView(generateNewFieldView(context, mainView), lParams);
    }

    public static final View generateNewFieldView(final Context context, final LinearLayout mainView){
        final int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(matchParent, wrapContent);
        final LinearLayout fieldLinearLayout = new LinearLayout(context);

        ViewGroup.LayoutParams editTextParams = new ViewGroup.LayoutParams(230, wrapContent);
        EditText fieldName = new EditText(context);
        fieldName.setHint("Field name");
        fieldName.setHintTextColor(Color.BLUE);
        fieldName.setTextColor(Color.BLACK);
        fieldName.setPadding(0, 0, 10, 0);
        fieldLinearLayout.addView(fieldName, editTextParams);

        ViewGroup.LayoutParams lenghtEditTextParams = new ViewGroup.LayoutParams(80, wrapContent);
        EditText lenghtEditText = new EditText(context);
        lenghtEditText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        lenghtEditText.setHint("lenght");
        lenghtEditText.setHintTextColor(Color.BLUE);
        lenghtEditText.setTextColor(Color.BLACK);
        lenghtEditText.setPadding(0, 0, 10, 0);
        lenghtEditText.setEnabled(false);
        fieldLinearLayout.addView(lenghtEditText, lenghtEditTextParams);

        ViewGroup.LayoutParams spinnerParams = new ViewGroup.LayoutParams(180, wrapContent);
        Spinner typesSpinner = new Spinner(context);
        typesSpinner.setAdapter(new ArrayAdapter<SqlTypes>(context, R.layout.spinner_item, SqlTypes.values()));
        typesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SqlTypes[] types = SqlTypes.values();

                if(types[i].equals(SqlTypes.VARCHAR)){
                    fieldLinearLayout.getChildAt(1).setEnabled(true);
                    ((EditText)fieldLinearLayout.getChildAt(1)).setHint("lenght");
                }else{
                    ((EditText)fieldLinearLayout.getChildAt(1)).setHint("");
                    fieldLinearLayout.getChildAt(1).setEnabled(false);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fieldLinearLayout.setPadding(0, 0, 10, 0);
        fieldLinearLayout.addView(typesSpinner, spinnerParams);

        ViewGroup.LayoutParams chbParams = new ViewGroup.LayoutParams(wrapContent, wrapContent);
        CheckBox primaryKeyChb = new CheckBox(context);
        primaryKeyChb.setText("primary key");
        primaryKeyChb.setTextColor(Color.BLACK);
        primaryKeyChb.setPadding(0, 0, 10, 0);
        fieldLinearLayout.addView(primaryKeyChb, chbParams);

        ViewGroup.LayoutParams deleteButtonParams = new ViewGroup.LayoutParams(wrapContent, wrapContent);
        ImageButton deleteFieldButton = new ImageButton(context);
        deleteFieldButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
        deleteFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainView.removeView(fieldLinearLayout);
            }
        });

        fieldLinearLayout.addView(deleteFieldButton, deleteButtonParams);

        return fieldLinearLayout;
    }

    public static final void addIssetFieldsInMainView(final Context context, final LinearLayout mainView, ArrayList<TableMetadataModel.Fields> fieldsArrayList){
        ArrayList<View> views = generateIssetFieldsView(context, mainView, fieldsArrayList);

        for(View view : views)
            mainView.addView(view);
    }

    public static final ArrayList<View> generateIssetFieldsView(final Context context, final LinearLayout mainView, ArrayList<TableMetadataModel.Fields> fieldsArrayList){
        ArrayList<View> views = new ArrayList<>();

        for(TableMetadataModel.Fields field : fieldsArrayList){
            View view = generateNewFieldView(context, mainView);

            fillValuesInFields(view, field);

            views.add(view);
        }

        return views;
    }

    private static final void fillValuesInFields(View view, TableMetadataModel.Fields field){
        SqlTypes[] types = SqlTypes.values();
        int position = Arrays.binarySearch(types, field.getType());

        EditText editText = (EditText) ((LinearLayout)view).getChildAt(0);
        editText.setText(field.getFieldName());

        EditText length = (EditText) ((LinearLayout)view).getChildAt(1);
        if(types[position].equals(SqlTypes.VARCHAR)){
            length.setText(String.valueOf(field.getLength()));
        }else{
            length.setHint("");
        }

        Spinner spinnerTypes = (Spinner) ((LinearLayout)view).getChildAt(2);
        spinnerTypes.setSelection(position);

        CheckBox primaryKey = (CheckBox) ((LinearLayout)view).getChildAt(3);
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

        final String finalPrimaryKey = primaryKey;
        ImageButton deleteRecordButton = (ImageButton)mainLayout.findViewById(R.id.deleteRecordButton);
        deleteRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectModel model = ConnectModel.getInstance("", "", "");
                model.setUserRequestToServer(MSSQLQueriesGenerator.deleteRecord(databaseName, tableName, primaryKeyFieldName, finalPrimaryKey));

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
                        finalPrimaryKey,
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

    public static final void createRecordView(Context context, TableLayout recordLayout, TableMetadataModel tableMetadata){
        int counter = 0;

        for(TableMetadataModel.Fields field : tableMetadata.getFieldsList()){
            TableRow row = new TableRow(context);

            TextView fieldView = new TextView(context);
            fieldView.setText(field.getFieldName());
            fieldView.setTextColor(Color.BLACK);
            fieldView.setTypeface(Typeface.DEFAULT_BOLD);
            fieldView.setMinWidth(150);
            row.addView(fieldView);

            EditText fieldValue = new EditText(context);
            fieldValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            fieldValue.setMaxLines(10);

            if(field.getLength() != 0)
                fieldValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(field.getLength())});

            fieldValue.setMinWidth(500);
            row.addView(fieldValue);

            if(counter%2 ==0)
                row.setBackgroundColor(Color.rgb(233,233,233));

            recordLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            counter++;
        }
    }

    public static final void fillRecordView(){}
}
