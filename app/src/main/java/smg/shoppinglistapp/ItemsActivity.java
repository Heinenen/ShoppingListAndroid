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

    ShoppingList shoppingList;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        setTitle(R.string.itemsAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shoppingList = new ShoppingList("test1");

        Item i1 = new Item("Milk", "drinks", 0, "1");
        Item i2 = new Item("Honey", "food", -1, "500g");
        Item i3 = new Item("Cookies", "food", 99, "20");

        shoppingList.addItemFS(i1);
        shoppingList.addItemFS(i2);
        shoppingList.addItemFS(i3);

        if(items == null){
            items = (ArrayList) shoppingList.getItems();
        }

        if(getIntent().hasExtra("smg.ITEMS")){
            items = (ArrayList) getIntent().getSerializableExtra("smg.ITEMS");
            shoppingList.setItems(items);
        }

        final Button addItemActivityBtn = findViewById(R.id.addItemActivityBtn);
        addItemActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemActivity = new Intent(ItemsActivity.this, AddItemActivity.class);
                addItemActivity.putExtra("smg.ITEMS", items);
                startActivity(addItemActivity);
            }
        });


        RecyclerView recyclerView = findViewById(R.id.secondRecyclerView);

        recyclerView.setAdapter(new ItemsAdapter(ItemsActivity.this, shoppingList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }
}
