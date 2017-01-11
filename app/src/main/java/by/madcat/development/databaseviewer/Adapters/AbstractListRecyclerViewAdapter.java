package by.madcat.development.databaseviewer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.R;

public abstract class AbstractListRecyclerViewAdapter extends RecyclerView.Adapter<AbstractListRecyclerViewAdapter.ViewHolder>{

    protected Context context;
    protected ArrayList<String> namesList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return (this.namesList != null) ? this.namesList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView text_view_for_name;
        ImageView overflow;

        public ViewHolder(View itemView) {
            super(itemView);

            text_view_for_name = (TextView)itemView.findViewById(R.id.text_view_for_name);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
        }
    }
}
