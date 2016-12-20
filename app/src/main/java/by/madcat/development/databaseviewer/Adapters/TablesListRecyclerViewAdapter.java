package by.madcat.development.databaseviewer.Adapters;

import android.content.Context;

import java.util.ArrayList;

public class TablesListRecyclerViewAdapter extends AbstractListRecyclerViewAdapter{

    public TablesListRecyclerViewAdapter(ArrayList<String> namesList, Context context){
        this.namesList = namesList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text_view_for_name.setText(this.namesList.get(position));
    }
}
