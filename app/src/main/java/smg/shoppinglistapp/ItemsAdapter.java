package smg.shoppinglistapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import smg.logic.Item;
import smg.logic.ShoppingList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<Item> items;
    private Context context;

    public ItemsAdapter(Context context, ShoppingList shoppingList){
        this.context = context;
        items = shoppingList.getItems();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.items_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Item item = items.get(position);


        holder.itemNameTextView.setText(item.getName());
        holder.itemCategoryTextView.setText(item.getCategory().getName());
        holder.itemAmountTextView.setText(item.getAmount());
        holder.itemPriorityTextView.setText(String.valueOf((item.getPriority())));
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView itemNameTextView;
        private TextView itemCategoryTextView;
        private TextView itemAmountTextView;
        private TextView itemPriorityTextView;
        private View parentView;

        public ViewHolder (@NonNull  View view){
            super(view);
            this.parentView = view;
            this.itemNameTextView = view.findViewById(R.id.itemNameTextView);
            this.itemCategoryTextView = view.findViewById(R.id.itemCategoryTextView);
            this.itemAmountTextView = view.findViewById(R.id.itemAmountTextView);
            this.itemPriorityTextView = view.findViewById(R.id.itemPriorityTextView);
        }
    }
}
