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
    private String shoppingList;
    private ArrayList<Item> items;

    public ItemsAdapter(Context context, String slID, String shoppingList){
        this.context = context;
        this.slID = slID;
        this.shoppingList = shoppingList;
        this.myDb = new DatabaseHelper(context);
        this.items = getItemsFromSQL();
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.details_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String itemPriorityText;
        if(items.get(position).getPriority().equals("0")){
            itemPriorityText = "X";
        } else {
            itemPriorityText = "✔";
        }


        holder.itemNameTextView.setText(items.get(position).getName());
        holder.itemCategoryTextView.setText(items.get(position).getCategory());
        holder.itemAmountTextView.setText(items.get(position).getAmount());
        holder.itemPriorityTextView.setText(itemPriorityText);
        holder.itemPriceTextView.setText(items.get(position).getPrice());

        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent editItemIntent = new Intent(context, EditItemActivity.class);
                String itemID = items.get(holder.getAdapterPosition()).getId();
                editItemIntent.putExtra("smg.SL_ID", slID);
                editItemIntent.putExtra("smg.SHOPPING_LIST", shoppingList);
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
        private TextView itemPriceTextView;
//        private CheckBox checkBox;
        private View parentView;

        public ViewHolder (@NonNull  View view){
            super(view);
            this.itemNameTextView = view.findViewById(R.id.itemNameTextView);
            this.itemCategoryTextView = view.findViewById(R.id.itemCategoryTextView);
            this.itemAmountTextView = view.findViewById(R.id.itemAmountTextView);
            this.itemPriorityTextView = view.findViewById(R.id.itemPriorityTextView);
            this.itemPriceTextView = view.findViewById(R.id.itemPriceTextView);
//            this.checkBox = view.findViewById(R.id.itemCheckBox);
            this.parentView = view;
        }
    }

    public ArrayList<Item> getItems(){
        return this.items;
    }

    public void setItems(ArrayList<Item> items){
        this.items = items;
    }


    public ArrayList<Item> getItemsFromSQL() {
        Cursor res = myDb.getItems(slID);
        ArrayList<Item> list = new ArrayList<>();

        while (res.moveToNext()) {
            list.add(new Item(res.getString(0), res.getString(2) , res.getString(3), res.getString(4), res.getString(5), res.getString(6)));
        }

        return list;
    }
}
