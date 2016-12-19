package by.madcat.development.databaseviewer.Adapters;

import android.content.Context;
import android.content.Intent;
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

import by.madcat.development.databaseviewer.AddEditDatabaseActivityApplicationActivity;
import by.madcat.development.databaseviewer.ConnectData.ConnectModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.Utils.QueriesList;

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

                        Intent intent;

                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                intent = AddEditDatabaseActivityApplicationActivity.getIntent(
                                        context,
                                        AddEditDatabaseActivityApplicationActivity.DATABASE_EDIT,
                                        holder.database_name.getText().toString());
                                context.startActivity(intent);
                                return true;
                            case R.id.action_delete:
                                ConnectModel connectModel = ConnectModel.getInstance("", "", "");
                                connectModel.setUserRequestToServer(QueriesList.DATABASE_DELETE +
                                        holder.database_name.getText().toString());

                                intent = new Intent(context, RequestService.class);
                                intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
                                intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
                                intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
                                intent.putExtra(RequestService.EXECUTE_MODEL, false);
                                context.startService(intent);

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
