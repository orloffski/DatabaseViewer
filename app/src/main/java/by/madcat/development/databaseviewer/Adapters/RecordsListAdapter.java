package by.madcat.development.databaseviewer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableRow;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Utils.ViewGenerator;

public class RecordsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String[]> records;
    private LayoutInflater lInflater;

    private String databaseName;
    private String tableName;

    public RecordsListAdapter(Context context, ArrayList<String[]> records, String databaseName, String tableName){
        this.context = context;
        this.records = records;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TableRow row;
        View view;

        if (convertView == null) {
            view =  lInflater.inflate(R.layout.records_line, parent, false);
        } else {
            view = convertView;
        }

        row = (TableRow) view.findViewById(R.id.record_line);

        ViewGenerator.createViewRecordLine(context, row, records.get(position), (position == 0 ? true : false), position, databaseName, tableName);

        return row;
    }
}
