package by.madcat.development.databaseviewer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.ActivitiesUI.AddEditDatabaseActivity;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.ActivitiesUI.TablesListActivity;

public class DatabasesListRecyclerViewAdapter extends AbstractListRecyclerViewAdapter{

    public DatabasesListRecyclerViewAdapter(ArrayList<String> namesList, Context context){
        this.namesList = namesList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.text_view_for_name.setText(this.namesList.get(position));

        holder.text_view_for_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TablesListActivity.getIntent(context, namesList.get(position));
                context.startActivity(intent);
            }
        });

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
                                intent = AddEditDatabaseActivity.getIntent(
                                        context,
                                        AddEditDatabaseActivity.DATABASE_EDIT,
                                        holder.text_view_for_name.getText().toString());
                                context.startActivity(intent);
                                return true;
                            case R.id.action_delete:
                                ConnectModel connectModel = ConnectModel.getInstance("", null, "", "");
                                connectModel.setUserRequestToServer(connectModel.getQueriesGenerator().deleteDatabase(
                                        holder.text_view_for_name.getText().toString()
                                ));

                                intent = new Intent(context, RequestService.class);
                                intent.putExtra(RequestService.SERVER_IP_ADRESS, connectModel.getServerIpAdress());
                                intent.putExtra(RequestService.USER_NAME, connectModel.getUserName());
                                intent.putExtra(RequestService.USER_PASSWORD, connectModel.getUserPassword());
                                intent.putExtra(RequestService.EXECUTE_MODEL, 1);
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
}
