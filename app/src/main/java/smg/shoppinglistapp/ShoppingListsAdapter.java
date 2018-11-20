package smg.shoppinglistapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import smg.logic.ShoppingList;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder> {

    private List<ShoppingList> shoppingLists;
    private Context context;

    public ShoppingListsAdapter(Context context, List<ShoppingList> shoppingLists){
        this.context = context;
        this.shoppingLists = shoppingLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.shopping_lists_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ShoppingList shoppingList = shoppingLists.get(position);

        holder.nameTextView.setText(shoppingList.getName());
        holder.descriptionTextView.setText(shoppingList.getName() + "-description");
        holder.priceTextView.setText("test!");
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemsActivity.class);
                intent.putExtra("smg.SHOPPING_LISTS", shoppingList);
                intent.putExtra("smg.INDEX", holder.getAdapterPosition());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.shoppingLists.size();
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
}
