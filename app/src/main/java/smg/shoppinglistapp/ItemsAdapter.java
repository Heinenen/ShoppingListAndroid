package smg.shoppinglistapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import smg.logic.Item;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private Context context;
    private DatabaseHelper myDb;
    private String slID;
    private ArrayList<Item> items;

    public ItemsAdapter(Context context, String slID){
        this.context = context;
        this.slID = slID;
        this.myDb = new DatabaseHelper(context);
        this.items = getItemsFromSQL();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.items_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String itemID = items.get(position).getId();

        String itemPriorityText;
        if(items.get(position).getPriority().equals("0")){
            itemPriorityText = "X";
        } else {
            itemPriorityText = "✔";
        }


        holder.itemNameTextView.setText(items.get(position).getName());
        holder.itemCategoryTextView.setText(items.get(position).getCategory());
        holder.itemPriorityTextView.setText(itemPriorityText);
        holder.itemAmountTextView.setText(items.get(position).getAmount());

        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent editItemIntent = new Intent(context, EditItemActivity.class);
                editItemIntent.putExtra("smg.SL_ID", slID);
                editItemIntent.putExtra("smg.ITEM_ID", itemID);
                context.startActivity(editItemIntent);
                if(context instanceof ItemsActivity){
                    ((ItemsActivity) context).callOnSaveInstanceState(new Bundle());
                    ((ItemsActivity) context).finish();
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView itemNameTextView;
        private TextView itemCategoryTextView;
        private TextView itemAmountTextView;
        private TextView itemPriorityTextView;
        private View parentView;

        public ViewHolder (@NonNull  View view){
            super(view);
            this.itemNameTextView = view.findViewById(R.id.itemNameTextView);
            this.itemCategoryTextView = view.findViewById(R.id.itemCategoryTextView);
            this.itemAmountTextView = view.findViewById(R.id.itemAmountTextView);
            this.itemPriorityTextView = view.findViewById(R.id.itemPriorityTextView);
            this.parentView = view;
        }
    }


    public ArrayList<Item> getItemsFromSQL() {
        Cursor res = myDb.getItems(slID);
        ArrayList<Item> list = new ArrayList<>();

        while (res.moveToNext()) {
            list.add(new Item(res.getString(0), res.getString(2) , res.getString(3), res.getString(4), res.getString(5)));
        }

        return list;
    }
}
