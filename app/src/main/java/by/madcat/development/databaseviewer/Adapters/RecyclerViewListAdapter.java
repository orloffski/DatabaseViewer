package by.madcat.development.databaseviewer.Adapters;

import android.content.Context;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.R;

public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.ViewHolder> {

    private ArrayList<String> databases;
    private Context context;

    public RecyclerViewListAdapter(ArrayList<String> databases, Context context){
        this.databases = databases;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.database_name.setText(this.databases.get(position));

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.overflow);
                popup.inflate(R.menu.menu_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.action_delete:
                                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (this.databases != null) ? this.databases.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView database_name;
        ImageView overflow;

        public ViewHolder(View itemView) {
            super(itemView);

            database_name = (TextView)itemView.findViewById(R.id.database_name);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
        }
    }
}
