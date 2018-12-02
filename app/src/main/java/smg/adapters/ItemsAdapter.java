package smg.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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

import smg.interfaces.AdapterCallActivityMethod;
import smg.models.Item;
import smg.shoppinglistapp.DatabaseHelper;
import smg.shoppinglistapp.R;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.CustomViewHolder> {

    private Context context;
    private AdapterCallActivityMethod parentActivity;
    private DatabaseHelper myDb;
    private String slID;
    private String shoppingList;
    private ArrayList<Item> items;
    private ArrayList<Item> selectedItems;
    private int[] rowIndices;

    public ItemsAdapter(Context context, AdapterCallActivityMethod parentActivity, String slID, String shoppingList){
        this.context = context;
        this.parentActivity = parentActivity;
        this.slID = slID;
        this.shoppingList = shoppingList;
        this.myDb = new DatabaseHelper(context);
        this.items = getItemsFromSQL();
        this.selectedItems = new ArrayList<>();
        this.rowIndices = new int[items.size()];
        Arrays.fill(rowIndices, -1);

    }

    // TODO set default color for text and background (not hardcoded black)

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        CustomViewHolder mHolder = new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false));
        // TODO move setOnClickListener to here, also for other things
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        final Item item = items.get(holder.getAdapterPosition());
        holder.itemNameTextView.setText(item.getName());
        holder.itemCategoryTextView.setText(item.getCategory());
        holder.itemAmountTextView.setText(item.getAmount());
        holder.itemPriceTextView.setText(item.getPrice());
        holder.itemCheckBox.setChecked(item.isCheck());


        if(items.get(holder.getAdapterPosition()).getPriority().equals("1")){
            holder.itemView.setBackgroundColor(Color.parseColor("#ce4848"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

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
            }
        });


        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deSelectItem(holder.getAdapterPosition(), item);
                return true;
            }
        });

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


        if(rowIndices[holder.getAdapterPosition()] == holder.getAdapterPosition() && items.get(holder.getAdapterPosition()).getPriority().equals("1")) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemImportantMarked));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantMarkedText));
        } else if(rowIndices[holder.getAdapterPosition()] == holder.getAdapterPosition()){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemMarked));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(context, R.color.itemMarkedText));
        } else if(items.get(holder.getAdapterPosition()).getPriority().equals("1")){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemImportant));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(context, R.color.itemImportantText));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.itemDefault));
            holder.itemNameTextView.setTextColor(ContextCompat.getColor(context, R.color.itemDefaultText));
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public void deSelectItem(int position, Item item){
        if (rowIndices[position] == -1){
            rowIndices[position] = position;
            selectedItems.add(item);
        } else {
            rowIndices[position] = -1;
            selectedItems.remove(item);
        }
        notifyDataSetChanged();

        boolean[] booleans = checkForToolbarButtonVisibility();
        parentActivity.refreshToolbar(booleans[0], booleans[1]);
    }


    public boolean[] checkForToolbarButtonVisibility(){
        int itemsSelectedCounter = 0;

        for(int i = 0; i < rowIndices.length; i++){
            if(rowIndices[i] != -1){
                itemsSelectedCounter = itemsSelectedCounter + 1;
                if(itemsSelectedCounter > 1) break;
            }
        }

        if(itemsSelectedCounter == 1){
            return new boolean[]{true, true};
        } else if(itemsSelectedCounter > 1){
            return new boolean[]{true, false};
        } else {
            return  new boolean[]{false, false};
        }
    }


    public ArrayList<Item> getItems(){
        return this.items;
    }

    public ArrayList<Item> getSelectedItems(){
        return this.selectedItems;
    }

    public void deleteItemFromList(Item item){
        this.items.remove(item);
    }

    public void deselectAll(){
        Arrays.fill(rowIndices, -1);
    }


    public ArrayList<Item> getItemsFromSQL() {
        Cursor res = myDb.getItems(slID);
        ArrayList<Item> list = new ArrayList<>();

        while (res.moveToNext()) {
            list.add(new Item(
                    res.getString(0),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5),
                    res.getString(6),
                    res.getInt(7)));

        }

        return list;
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
