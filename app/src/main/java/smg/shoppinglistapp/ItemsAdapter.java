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

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private Context context;
    private DatabaseHelper myDb;
    private String slID;

    public ItemsAdapter(Context context, String slID){
        this.context = context;
        this.slID = slID;
        myDb = new DatabaseHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.items_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ArrayList<String>[] strings = getItems();


        holder.itemNameTextView.setText(strings[1].get(position));
        holder.itemCategoryTextView.setText(strings[2].get(position));
        holder.itemAmountTextView.setText(strings[3].get(position));
        holder.itemPriorityTextView.setText(strings[4].get(position));

        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent editItemIntent = new Intent(context, EditItemActivity.class);
                editItemIntent.putExtra("smg.SL_ID", slID);
                editItemIntent.putExtra("smg.ITEM_ID", Integer.toString(position + 1));
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
        ArrayList<String>[] strings = getItems();

        return strings[0].size();
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

    public ArrayList[] getItems() {
        Cursor res = myDb.getItems(slID);
        ArrayList<String> idStrings = new ArrayList<>();
        ArrayList<String> nameStrings = new ArrayList<>();
        ArrayList<String> categoryStrings = new ArrayList<>();
        ArrayList<String> priorityStrings = new ArrayList<>();
        ArrayList<String> amountStrings = new ArrayList<>();

        while (res.moveToNext()) {
            idStrings.add(res.getString(0));            // item_id
            nameStrings.add(res.getString(2));          // item_name
            categoryStrings.add(res.getString(3));      // item_category
            priorityStrings.add(res.getString(4));      // item_priority
            amountStrings.add(res.getString(5));        // item_amount
        }

        return new ArrayList[]{idStrings, nameStrings, categoryStrings, priorityStrings, amountStrings};
    }
}
