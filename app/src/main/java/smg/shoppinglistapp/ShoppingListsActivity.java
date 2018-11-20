package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;

import smg.logic.ShoppingList;

public class ShoppingListsActivity extends AppCompatActivity implements Serializable {


    // TODO add saving of shoppingLists
    ArrayList<ShoppingList> shoppingLists;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_lists);
        setTitle(R.string.shoppingListsAct_title);


        if(shoppingLists == null){
            shoppingLists = new ArrayList<>();
        }

        if(getIntent().hasExtra("smg.SHOPPING_LISTS")){
            shoppingLists = (ArrayList) getIntent().getSerializableExtra("smg.SHOPPING_LISTS");
        }

        Button addShoppingListActivityBtn = findViewById(R.id.addShoppingListActivityBtn);
        addShoppingListActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addShoppingList = new Intent(ShoppingListsActivity.this, AddShoppingListActivity.class);
                addShoppingList.putExtra("smg.SHOPPING_LISTS", shoppingLists);
                startActivity(addShoppingList);
            }
        });


        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);

        recyclerView.setAdapter(new ShoppingListsAdapter(ShoppingListsActivity.this, shoppingLists));
        recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingListsActivity.this));
    }
}
