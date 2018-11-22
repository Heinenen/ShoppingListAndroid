package smg.shoppinglistapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder> {

    private Context context;
    private DatabaseHelper myDb;

    public ShoppingListsAdapter(Context context){
        this.context = context;

        myDb = new DatabaseHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder (LayoutInflater.from(context).inflate(R.layout.shopping_lists_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        ArrayList<String>[] strings = getShoppingLists();
        final String shoppingList = strings[1].get(position);

        holder.nameTextView.setText(strings[1].get(position));
        holder.descriptionTextView.setText("description");
        holder.priceTextView.setText(strings[0].get(position));
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemsActivity.class);
                intent.putExtra("smg.INDEX", holder.getAdapterPosition());
                intent.putExtra("smg.SHOPPING_LIST", shoppingList);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        ArrayList<String>[] strings = getShoppingLists();

        return strings[0].size();
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

    public ArrayList<String>[] getShoppingLists(){
        Cursor res = myDb.getSL();
        ArrayList<String> idStrings = new ArrayList<>();
        ArrayList<String> nameStrings = new ArrayList<>();

        while (res.moveToNext()) {
            idStrings.add(res.getString(0));    // id
            nameStrings.add(res.getString(1));     // name

        }

        return new ArrayList[]{idStrings, nameStrings};
    }

}

//    public void viewAll() {
//        btnviewAll.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Cursor res = myDb.getAllData();
//                        if(res.getCount() == 0) {
//                            // show message
//                            showMessage("Error","Nothing found");
//                            return;
//                        }
//
//                        StringBuffer buffer = new StringBuffer();
//                        while (res.moveToNext()) {
//                            buffer.append("Id: "+ res.getString(0)+"\n");
//                            buffer.append("name: "+ res.getString(1)+"\n");
//                            buffer.append("cat: "+ res.getString(2)+"\n");
//                            buffer.append("prio: "+ res.getString(3)+"\n");
//                            buffer.append("amount: "+ res.getString(4)+"\n\n");
//                        }
//
//                        // Show all data
//                        showMessage("Data",buffer.toString());
//                    }
//                }
//        );
//    }

//    public void showMessage(String title,String Message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(Message);
//        builder.show();
//    }
