package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;

import smg.logic.Item;

public class AddItemActivity extends AppCompatActivity implements Serializable {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        setTitle(R.string.addItemAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Button addShoppingList = findViewById(R.id.addItemBtn);
        addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText itemName = findViewById(R.id.itemNameEditText);
                EditText itemCategory = findViewById(R.id.itemCategoryEditText);
                EditText itemPriority = findViewById(R.id.itemPriorityEditText);
                EditText itemAmount = findViewById(R.id.itemAmountEditText);
                ArrayList<Item> items = (ArrayList) getIntent().getSerializableExtra("smg.ITEMS");

                // Item parameters
                // TODO make program complain if no name is given

                // default value for name (-> Item)
                String itemNameString;
                if(itemName.getText().toString().equals("")){
                    itemNameString = getString(R.string.addItemAct_defaultItemName);
                } else {
                    itemNameString = itemName.getText().toString();
                }

                // default value for category (-> None)
                String itemCategoryCategory;
                if(itemCategory.getText().toString().equals("")){
                    itemCategoryCategory = getString(R.string.addItemAct_defaultCategoryName);
                } else {
                    itemCategoryCategory = itemCategory.getText().toString();
                }

                // default value for priority (-> 0)
                int itemPriorityInt;
                if(itemPriority.getText().toString().equals("")) {
                    itemPriorityInt = 0;
                } else {
                    itemPriorityInt = Integer.parseInt(itemPriority.getText().toString());
                }

                // default value for amount (-> 1)
                String itemAmountString;
                if(itemAmount.getText().toString().equals("")){
                    itemAmountString = getString(R.string.addItemAct_defaultAmount);
                } else {
                    itemAmountString = itemAmount.getText().toString();
                }

                Item item = new Item(itemNameString, itemCategoryCategory, itemPriorityInt, itemAmountString);
                items.add(item);

                Intent itemsActivity = new Intent(AddItemActivity.this, ItemsActivity.class);
                itemsActivity.putExtra("smg.ITEMS", items);
                startActivity(itemsActivity);
            }
        });
    }
}
