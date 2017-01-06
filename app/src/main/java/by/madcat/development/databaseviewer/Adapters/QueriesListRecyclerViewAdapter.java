package by.madcat.development.databaseviewer.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.SQLiteData.DatabaseDescription;

public class QueriesListRecyclerViewAdapter extends RecyclerView.Adapter<QueriesListRecyclerViewAdapter.ViewHolder>{

    public interface ClickListener{
        void onClick(Uri queryUri);
    }

    protected Cursor cursor;
    protected Context context;
    private final ClickListener clickListener;

    public QueriesListRecyclerViewAdapter(ClickListener clickListener, Context context){
        this.clickListener = clickListener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setRowID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseDescription.Query._ID))));

        holder.text_view_for_name.setText(cursor.getString(cursor.getColumnIndex(DatabaseDescription.Query.QUERY_NAME)));

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
                                return true;
                            case R.id.action_delete:
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
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private long rowID;

        TextView text_view_for_name;
        ImageView overflow;

        public ViewHolder(View itemView, final ClickListener clickListener) {
            super(itemView);

            text_view_for_name = (TextView)itemView.findViewById(R.id.text_view_for_name);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);

            text_view_for_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(DatabaseDescription.Query.buildQueriesUri((int)rowID));
                }
            });
        }

        public void setRowID(long id){
            this.rowID = id;
        }
    }

    public void swapCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
