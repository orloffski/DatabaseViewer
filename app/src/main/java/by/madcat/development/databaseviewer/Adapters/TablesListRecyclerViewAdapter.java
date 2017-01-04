package by.madcat.development.databaseviewer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.ArrayList;

import by.madcat.development.databaseviewer.AddEditTableActivity;
import by.madcat.development.databaseviewer.Models.ConnectModel;
import by.madcat.development.databaseviewer.R;
import by.madcat.development.databaseviewer.Requests.RequestService;
import by.madcat.development.databaseviewer.QueriesGenerators.MSSQLQueriesPartsList;

public class TablesListRecyclerViewAdapter extends AbstractListRecyclerViewAdapter{

    private String databaseName;

    public TablesListRecyclerViewAdapter(ArrayList<String> namesList, Context context, String databaseName){
        this.namesList = namesList;
        this.context = context;
        this.databaseName = databaseName;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.text_view_for_name.setText(this.namesList.get(position));

        /*
        // onClick to open records list

        holder.text_view_for_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TablesListActivity.getIntent(context, namesList.get(position));
                context.startActivity(intent);
            }
        });
        */

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
                                intent = AddEditTableActivity.getIntent(
                                        context,
                                        AddEditTableActivity.TABLE_EDIT,
                                        holder.text_view_for_name.getText().toString(),
                                        databaseName);
                                context.startActivity(intent);

                                return true;
                            case R.id.action_delete:
                                ConnectModel connectModel = ConnectModel.getInstance("", "", "");
                                connectModel.setUserRequestToServer(String.format(MSSQLQueriesPartsList.TABLE_DELETE,
                                        databaseName, holder.text_view_for_name.getText().toString()));

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
