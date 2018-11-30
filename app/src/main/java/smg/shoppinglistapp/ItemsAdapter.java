package smg.shoppinglistapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import smg.logic.Item;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.CustomViewHolder> {

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

    // TODO set default color for text and background (not hardcoded black)

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        CustomViewHolder mHolder = new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false));
//        mHolder.itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });
        // TODO move setOnClickListener to here, also for other things
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        holder.itemNameTextView.setText(items.get(holder.getAdapterPosition()).getName());
        holder.itemCategoryTextView.setText(items.get(holder.getAdapterPosition()).getCategory());
        holder.itemAmountTextView.setText(items.get(holder.getAdapterPosition()).getAmount());
        holder.itemPriceTextView.setText(items.get(holder.getAdapterPosition()).getPrice());
        holder.itemCheckBox.setChecked(items.get(holder.getAdapterPosition()).isCheck());


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
                Intent editItemIntent = new Intent(context, EditItemActivity.class);
                String itemID = items.get(holder.getAdapterPosition()).getId();
                editItemIntent.putExtra("smg.SL_ID", slID);
                editItemIntent.putExtra("smg.SHOPPING_LIST", shoppingList);
                editItemIntent.putExtra("smg.ITEM_ID", itemID);
                context.startActivity(editItemIntent);
//                if(context instanceof ItemsActivity){
//                    ((ItemsActivity) context).callOnSaveInstanceState(new Bundle());
//                    ((ItemsActivity) context).finish();
//                }
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public ArrayList<Item> getItems(){
        return this.items;
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
