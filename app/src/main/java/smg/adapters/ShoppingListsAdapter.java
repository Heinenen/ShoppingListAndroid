package smg.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import smg.interfaces.ShoppingListsAdapterInterface;
import smg.models.ShoppingList;
import smg.shoppinglistapp.ItemsActivity;
import smg.shoppinglistapp.R;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder> {

    private Context context;
    private ShoppingListsAdapterInterface parentActivity;
    private ArrayList<ShoppingList> shoppingLists;
    private ArrayList<ShoppingList> selectedShoppingLists;
    private int[] rowIndices;

    public ShoppingListsAdapter(Context context, ShoppingListsAdapterInterface parentActivity, ArrayList<ShoppingList> shoppingLists){
        this.context = context;
        this.parentActivity = parentActivity;
        this.shoppingLists = shoppingLists;
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
        // sets value(s) to shown shopping list
        final ShoppingList shoppingList = shoppingLists.get(position);
        final String slID = shoppingList.getSlID();
        final String shoppingListName = shoppingList.getName();
        final int color = shoppingList.getColor();
        holder.nameTextView.setText(shoppingListName);

        // deSelects SL on longClick
        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deSelectShoppingList(holder.getAdapterPosition(), shoppingList);
                return true;
            }
        });

        // deSelects SL on click if at least one is already selected
        // if none selected, goes to corresponding ItemsActivity
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean shoppingListSelectedChecker = false;
                for(int i = 0; i < rowIndices.length; i++){
                    if(rowIndices[i] != -1){
                        shoppingListSelectedChecker = true;
                        break;
                    }
                }

                if(shoppingListSelectedChecker && holder.getAdapterPosition() != -1){
                    deSelectShoppingList(holder.getAdapterPosition(), shoppingList);
                } else {
                    Intent itemActivityIntent = new Intent(context, ItemsActivity.class);
                    itemActivityIntent.putExtra("smg.SL_ID", slID);
                    itemActivityIntent.putExtra("smg.SHOPPING_LIST", shoppingListName);
                    itemActivityIntent.putExtra("smg.COLOR", color);
                    context.startActivity(itemActivityIntent);
                }
            }
        });

        // sets colors depending on marked/not marked
        if(rowIndices[holder.getAdapterPosition()] == holder.getAdapterPosition()){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.shoppingListMarked));
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.shoppingListMarkedText));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(shoppingList.getColor())));
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.shoppingListDefaultText));
        }

    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }


    // method for deSelecting SLs
    private void deSelectShoppingList(int position, ShoppingList shoppingList){
        if (rowIndices[position] == -1){
            rowIndices[position] = position;
            selectedShoppingLists.add(shoppingList);
        } else {
            rowIndices[position] = -1;
            selectedShoppingLists.remove(shoppingList);
        }
        notifyDataSetChanged();

        boolean[] booleans = checkForToolbarButtonVisibility();
        parentActivity.refreshToolbar(booleans[0], booleans[1]);
    }

    // deselects all SLs
    public void deselectAll(){
        refreshRowIndices();
        selectedShoppingLists.clear();
    }

    public void refreshRowIndices(){
        this.rowIndices = new int[shoppingLists.size()];
        Arrays.fill(rowIndices, -1);
    }

    // method that checks which ToolbarButtons should be shown
    // depending on (how many) items selected
    public boolean[] checkForToolbarButtonVisibility(){
        int shoppingListsSelectedCounter = 0;

        for(int i = 0; i < rowIndices.length; i++){
            if(rowIndices[i] != -1){
                shoppingListsSelectedCounter = shoppingListsSelectedCounter + 1;
                if(shoppingListsSelectedCounter > 1) break;
            }
        }

        if(shoppingListsSelectedCounter == 1){
            return new boolean[]{true, true};
        } else if(shoppingListsSelectedCounter > 1){
            return new boolean[]{true, false};
        } else {
            return  new boolean[]{false, false};
        }
    }

    // getters
    public ArrayList<ShoppingList> getSelectedShoppingLists(){
        return this.selectedShoppingLists;
    }

    public int[] getRowIndices() {
        return rowIndices;
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