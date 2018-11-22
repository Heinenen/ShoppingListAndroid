package smg.shoppinglistapp;

import android.content.Context;
import android.database.Cursor;
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
    private String SL;

    public ItemsAdapter(Context context, String SL){
        this.context = context;
        this.SL = SL;

        myDb = new DatabaseHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.items_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<String>[] strings = getItems();


        holder.itemNameTextView.setText(strings[1].get(position));
        holder.itemCategoryTextView.setText(strings[2].get(position));
        holder.itemAmountTextView.setText(strings[3].get(position));
        holder.itemPriorityTextView.setText(strings[4].get(position));
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

        public ViewHolder (@NonNull  View view){
            super(view);
            this.itemNameTextView = view.findViewById(R.id.itemNameTextView);
            this.itemCategoryTextView = view.findViewById(R.id.itemCategoryTextView);
            this.itemAmountTextView = view.findViewById(R.id.itemAmountTextView);
            this.itemPriorityTextView = view.findViewById(R.id.itemPriorityTextView);
        }
    }

    public ArrayList[] getItems() {
        Cursor res = myDb.getItems(SL);
        ArrayList<String> idStrings = new ArrayList<>();
        ArrayList<String> nameStrings = new ArrayList<>();
        ArrayList<String> categoryStrings = new ArrayList<>();
        ArrayList<String> priorityStrings = new ArrayList<>();
        ArrayList<String> amountStrings = new ArrayList<>();

        while (res.moveToNext()) {
            idStrings.add(res.getString(0));            // id
            nameStrings.add(res.getString(2));          // item_name
            categoryStrings.add(res.getString(3));      // item_category
            priorityStrings.add(res.getString(4));      // item_priority
            amountStrings.add(res.getString(5));        // item_amount
        }

        return new ArrayList[]{idStrings, nameStrings, categoryStrings, priorityStrings, amountStrings};
    }
}