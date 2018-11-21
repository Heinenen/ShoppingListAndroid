package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import smg.logic.ShoppingList;

public class AddShoppingListActivity extends AppCompatActivity implements Serializable {

    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list);
        setTitle(R.string.addShoppingListAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        myDb = new DatabaseHelper(this);

//        Cursor res = myDb.getAllData();
//        StringBuffer buffer = new StringBuffer();
//                        while (res.moveToNext()) {
//                            buffer.append("Id: "+ res.getString(0)+"\n");
//                            buffer.append("name: "+ res.getString(1)+"\n");
//                        }
//
//                        // Show all data
//                        showMessage("Data",buffer.toString());



        Button addShoppingList = findViewById(R.id.addShoppingListBtn);
        addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText shoppingListName = findViewById(R.id.shoppingListNameEditText);
                ArrayList<ShoppingList> shoppingLists = (ArrayList) getIntent().getSerializableExtra("smg.SHOPPING_LISTS");
                shoppingLists.add(new ShoppingList(shoppingListName.getText().toString()));

                boolean isInserted = myDb.addSL(shoppingListName.getText().toString());
                if (isInserted) {
                    Toast.makeText(AddShoppingListActivity.this, "Shopping list added", Toast.LENGTH_LONG).show();
                    Intent shoppingListsActivity = new Intent(AddShoppingListActivity.this, ShoppingListsActivity.class);
                    startActivity(shoppingListsActivity);
                } else {
                    Toast.makeText(AddShoppingListActivity.this, "Adding failed", Toast.LENGTH_LONG).show();
                }

                Intent shoppingListsActivity = new Intent(AddShoppingListActivity.this, ShoppingListsActivity.class);
                shoppingListsActivity.putExtra("smg.SHOPPING_LISTS", shoppingLists);
                startActivity(shoppingListsActivity);
            }
        });

    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}

/*
    }
        boolean isInserted = myDb.addSL(shoppingListName.getText().toString());
        if (isInserted) {
            Toast.makeText(AddShoppingListActivity.this, "Shopping list added", Toast.LENGTH_LONG).show();
            Intent shoppingListsActivity = new Intent(AddShoppingListActivity.this, ShoppingListsActivity.class);
            startActivity(shoppingListsActivity);
        } else {
            Toast.makeText(AddShoppingListActivity.this, "Adding failed", Toast.LENGTH_LONG).show();
        }
    }
});
}
}

//    public  void addItem() {
//        btnAddData.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        boolean isInserted = myDb.addItem("iname", "cat", 2, "5g");
//                        if(isInserted)
//                            Toast.makeText(ShoppingListsActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
//                        else
//                            Toast.makeText(ShoppingListsActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();
//                    }
//                }
//        );
//    }
//
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
//
//    public void showMessage(String title,String Message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(Message);
//        builder.show();
//    }
*/
