package smg.shoppinglistapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import smg.logic.ShoppingList;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder> {

    private Context context;
    private DatabaseHelper myDb;
    private ArrayList<ShoppingList> shoppingLists;

    public ShoppingListsAdapter(Context context){
        this.context = context;

        this.myDb = new DatabaseHelper(context);
        this.shoppingLists = getSLFromSQL();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.shopping_lists_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String slID = shoppingLists.get(position).getPosition();
        final String shoppingList = shoppingLists.get(position).getName();

        holder.nameTextView.setText(shoppingLists.get(position).getName());
        holder.descriptionTextView.setText("description");
        holder.priceTextView.setText(shoppingLists.get(position).getPosition());

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itemActivityIntent = new Intent(context, ItemsActivity.class);
                itemActivityIntent.putExtra("smg.SL_ID", slID);
                context.startActivity(itemActivityIntent);
            }
        });

        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent editSLIntent = new Intent(context, EditShoppingListActivity.class);
                editSLIntent.putExtra("smg.SL_ID", slID);
                editSLIntent.putExtra("smg.SHOPPING_LIST", shoppingList);
                context.startActivity(editSLIntent);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView priceTextView;
        private View parentView;

        public ViewHolder (@NonNull  View view){
            super(view);
            this.parentView = view;
            this.nameTextView = view.findViewById(R.id.nameTextView);
            this.descriptionTextView = view.findViewById(R.id.descriptionTextView);
            this.priceTextView = view.findViewById(R.id.priceTextView);

        }
    }

    public ArrayList<String>[] getShoppingLists(){
        Cursor res = myDb.getSL();
        ArrayList<String> idStrings = new ArrayList<>();
        ArrayList<String> nameStrings = new ArrayList<>();

        while (res.moveToNext()) {
            idStrings.add(res.getString(0));        // id
            nameStrings.add(res.getString(1));      // name

        }

        return new ArrayList[]{idStrings, nameStrings};
    }


    public ArrayList<ShoppingList> getSLFromSQL(){
        Cursor res = myDb.getSL();
        ArrayList<ShoppingList> list = new ArrayList<>();

        while (res.moveToNext()){
            list.add(new ShoppingList(res.getString(0), res.getString(1)));
        }

        return list;
    }

}