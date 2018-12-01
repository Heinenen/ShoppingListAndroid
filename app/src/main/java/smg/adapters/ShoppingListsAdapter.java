package smg.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import smg.models.ShoppingList;
import smg.shoppinglistapp.DatabaseHelper;
import smg.shoppinglistapp.ItemsActivity;
import smg.shoppinglistapp.R;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder> {

    private Context context;
    private DatabaseHelper myDb;
    private ArrayList<ShoppingList> shoppingLists;

    private ArrayList<ShoppingList> selectedShoppingLists;
    private int[] rowIndices;

    public ShoppingListsAdapter(Context context){
        this.context = context;
        this.myDb = new DatabaseHelper(context);
        this.shoppingLists = getSLFromSQL();
        this.selectedShoppingLists = new ArrayList<>();
        this.rowIndices = new int[shoppingLists.size()];
        Arrays.fill(rowIndices, -1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.layout_shopping_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ShoppingList shoppingList = shoppingLists.get(position);
        final String slID = shoppingList.getPosition();
        final String shoppingListName = shoppingList.getName();
        holder.nameTextView.setText(shoppingListName);

        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (rowIndices[holder.getAdapterPosition()] == -1){
                    rowIndices[holder.getAdapterPosition()] = holder.getAdapterPosition();
                    selectedShoppingLists.add(shoppingList);
                } else {
                    rowIndices[holder.getAdapterPosition()] = -1;
                    selectedShoppingLists.remove(shoppingList);
                }
                notifyDataSetChanged();
//                Intent editSLIntent = new Intent(context, EditShoppingListActivity.class);
//                editSLIntent.putExtra("smg.SL_ID", slID);
//                editSLIntent.putExtra("smg.SHOPPING_LIST", shoppingList);
//                context.startActivity(editSLIntent);
                return true;
            }
        });

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itemActivityIntent = new Intent(context, ItemsActivity.class);
                itemActivityIntent.putExtra("smg.SL_ID", slID);
                itemActivityIntent.putExtra("smg.SHOPPING_LIST", shoppingListName);
                context.startActivity(itemActivityIntent);
            }
        });


        if(rowIndices[holder.getAdapterPosition()] == holder.getAdapterPosition()){
            holder.itemView.setBackgroundColor(Color.parseColor("#000000"));
            holder.nameTextView.setTextColor(Color.parseColor("#c5c5c7"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.nameTextView.setTextColor(Color.parseColor("#000000"));
        }

    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }


    public ArrayList<ShoppingList> getSLFromSQL(){
        Cursor res = myDb.getSL();
        ArrayList<ShoppingList> list = new ArrayList<>();

        while (res.moveToNext()){
            list.add(new ShoppingList(res.getString(0), res.getString(1)));
        }

        return list;
    }

    public void deleteSLFromList(ShoppingList shoppingList){
        shoppingLists.remove(shoppingList);
    }

    public ArrayList<ShoppingList> getSelectedShoppingLists(){
        return this.selectedShoppingLists;
    }

    public void deselectAll(){
        Arrays.fill(rowIndices, -1);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private View parentView;

        public ViewHolder (@NonNull  View view){
            super(view);
            this.parentView = view;
            this.nameTextView = view.findViewById(R.id.nameTextView);

        }
    }
}