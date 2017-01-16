package by.madcat.development.databaseviewer.ActivitiesUI;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Utils.SqlTypes;

public class AddEditFieldView extends LinearLayout{
    public interface FieldDeleted{
        void fieldToDelete(String fieldName);
    }

    private Context context;
    private FieldDeleted fieldDeleted;

    private LinearLayout mainLayout;
    private EditText fieldName;
    private EditText lenghtEditText;
    private Spinner typesSpinner;
    private CheckBox primaryKeyChb;
    private ImageButton deleteFieldButton;

    public AddEditFieldView(Context context, final LinearLayout mainLayout, FieldDeleted fieldDeleted) {
        super(context);

        this.context = context;
        this.fieldDeleted = fieldDeleted;
        this.mainLayout = mainLayout;

        initFieldView();
    }

    private void initFieldView(){
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.add_edit_field_view,this);

        this.fieldName = (EditText)view.findViewById(R.id.fieldName);
        this.fieldName.setHintTextColor(Color.BLUE);
        this.fieldName.setTextColor(Color.BLACK);

        this.lenghtEditText = (EditText)view.findViewById(R.id.fieldLenght);
        this.lenghtEditText.setHintTextColor(Color.BLUE);
        this.lenghtEditText.setTextColor(Color.BLACK);

        this.typesSpinner = (Spinner)view.findViewById(R.id.typesSpinner);
        this.typesSpinner.setAdapter(new ArrayAdapter<>(this.context, R.layout.spinner_item, SqlTypes.values()));
        this.typesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SqlTypes[] types = SqlTypes.values();

                if(types[i].equals(SqlTypes.VARCHAR)){
                    lenghtEditText.setEnabled(true);
                    lenghtEditText.setHint("lenght");
                }else{
                    lenghtEditText.setHint("");
                    lenghtEditText.setEnabled(false);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        this.primaryKeyChb = (CheckBox)view.findViewById(R.id.primaryKeyChb);
        this.primaryKeyChb.setTextColor(Color.BLACK);

        this.deleteFieldButton = (ImageButton)view.findViewById(R.id.deleteFieldButton);
        this.deleteFieldButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
        this.deleteFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldDeleted.fieldToDelete(fieldName.getText().toString());

                deleteFieldLine();
            }
        });
    }

    private void deleteFieldLine(){
        mainLayout.removeView(this);
    }
}
