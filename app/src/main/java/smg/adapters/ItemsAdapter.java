package smg.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import smg.databasehelpers.DatabaseHelper;
import smg.interfaces.ItemsAdapterInterface;
import smg.models.Item;
import smg.shoppinglistapp.R;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.CustomViewHolder> {

    private Context context;
    private ItemsAdapterInterface parentActivity;
    private DatabaseHelper myDb;
    private ArrayList<Item> items;
    private ArrayList<Item> selectedItems;
    public boolean onBind;

    private int[] rowIndices;

    public ItemsAdapter(Context context, ItemsAdapterInterface parentActivity, ArrayList<Item> items){
        this.context = context;
        this.parentActivity = parentActivity;
        this.myDb = new DatabaseHelper(context);
        this.items = items;
//        this.items = getItemsFromSQL();
        this.selectedItems = new ArrayList<>();
        this.onBind = false;

        this.rowIndices = new int[items.size()];
        Arrays.fill(rowIndices, -1);

    }


    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        // sets values to shown item
        final Item item = items.get(holder.getAdapterPosition());
        holder.itemNameTextView.setText(item.getName());
        holder.itemCategoryTextView.setText(item.getCategory());
        holder.itemAmountTextView.setText(item.getAmount());
        holder.itemPriceTextView.setText(item.getPrice());
        onBind = true;
        holder.itemCheckBox.setChecked(item.isCheck());
        onBind = false;

        // saves value to SQL if it changes
        holder.itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int isCheckedInt;
                if(isChecked){
                    isCheckedInt = 1;
                } else {
                    isCheckedInt = 0;
                }

                items.get(holder.getAdapterPosition()).setCheck(isChecked);
                myDb.updateItemCheck(items.get(holder.getAdapterPosition()).getId(), isCheckedInt);
                if(!onBind){
                    parentActivity.sort();
                }
            }
        });

        // deSelects item on longClick
        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deSelectItem(holder.getAdapterPosition(), item);
                return true;
            }
        });

        // deSelects item on click if at least one is already selected
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean itemSelectedChecker = false;
                for(int i = 0; i < rowIndices.length; i++){
                    if(rowIndices[i] != -1){
                        itemSelectedChecker = true;
                        break;
                    }
                }

                if(itemSelectedChecker){
                    deSelectItem(holder.getAdapterPosition(), item);
                }
            }
        });

        // sets colors depending on marked/not marked
        if(!(rowIndices[holder.getAdapterPosition()] == holder.getAdapterPosition()) && !items.get(holder.getAdapterPosition()).getPriority().equals("1")) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemDefault));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(context, R.color.itemDefaultText));
            holder.itemCategoryTextView.setTextColor(ContextCompat.getColor(context, R.color.itemDefaultText));
            holder.itemAmountTextView.setTextColor(ContextCompat.getColor(context, R.color.itemDefaultText));
            holder.itemPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.itemDefaultText));
        } else if(rowIndices[holder.getAdapterPosition()] == holder.getAdapterPosition() && items.get(holder.getAdapterPosition()).getPriority().equals("1")) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemImportantMarked));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantMarkedText));
            holder.itemCategoryTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantMarkedText));
            holder.itemAmountTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantMarkedText));
            holder.itemPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantMarkedText));
        } else if(rowIndices[holder.getAdapterPosition()] == holder.getAdapterPosition()){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemMarked));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(context, R.color.itemMarkedText));
            holder.itemCategoryTextView.setTextColor(ContextCompat.getColor(context, R.color.itemMarkedText));
            holder.itemAmountTextView.setTextColor(ContextCompat.getColor(context, R.color.itemMarkedText));
            holder.itemPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.itemMarkedText));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemImportant));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantText));
            holder.itemCategoryTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantText));
            holder.itemAmountTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantText));
            holder.itemPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantText));
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    // method for deSelecting items
    public void deSelectItem(int position, Item item){
        if (rowIndices[position] == -1){
            rowIndices[position] = position;
            selectedItems.add(item);
        } else {
            rowIndices[position] = -1;
            selectedItems.remove(item);
        }
        notifyDataSetChanged();

        parentActivity.setSelectedItemsCount(selectedItems.size());
    }

    // deselects all items
    public void deselectAll(){
        Arrays.fill(rowIndices, -1);
        selectedItems.clear();
        parentActivity.setSelectedItemsCount(0);
    }

    public ArrayList<Item> getSelectedItems(){
        return this.selectedItems;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void refreshRowIndices(){
        this.rowIndices = new int[items.size()];
        Arrays.fill(rowIndices, -1);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        private TextView itemNameTextView;
        private TextView itemCategoryTextView;
        private TextView itemAmountTextView;
        private TextView itemPriceTextView;
        private CheckBox itemCheckBox;
        private View parentView;

        public CustomViewHolder (@NonNull  View view){
            super(view);
            this.itemNameTextView = view.findViewById(R.id.itemNameTextView);
            this.itemCategoryTextView = view.findViewById(R.id.itemCategoryTextView);
            this.itemAmountTextView = view.findViewById(R.id.itemAmountTextView);
            this.itemPriceTextView = view.findViewById(R.id.itemPriceTextView);
            this.itemCheckBox = view.findViewById(R.id.itemCheckBox);
            this.parentView = view;
        }
    }
}
