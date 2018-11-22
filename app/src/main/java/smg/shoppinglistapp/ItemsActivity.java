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

import smg.logic.Item;
import smg.logic.ShoppingList;


// TODO save shoppingLists somewhere
public class ItemsActivity extends AppCompatActivity implements Serializable {

    String shoppingList;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        setTitle(R.string.itemsAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");


        RecyclerView recyclerView = findViewById(R.id.secondRecyclerView);
        recyclerView.setAdapter(new ItemsAdapter(ItemsActivity.this, shoppingList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        addItemActivity();
    }

    public void addItemActivity(){
        Button addItemActivityBtn = findViewById(R.id.addItemActivityBtn);
        addItemActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemActivity = new Intent(ItemsActivity.this, AddItemActivity.class);
                addItemActivity.putExtra("smg.ITEMS", items);
                addItemActivity.putExtra("smg.SHOPPING_LIST", shoppingList);
                startActivity(addItemActivity);
            }
        });
    }
}
