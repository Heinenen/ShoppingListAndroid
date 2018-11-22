package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddItemActivity extends AppCompatActivity {


    private String shoppingList;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        setTitle(R.string.addItemAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");
        myDb = new DatabaseHelper(this);

        addItem();
    }


    public void addItem(){
        Button addItem = findViewById(R.id.addItemBtn);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText itemName = findViewById(R.id.itemNameEditText);
                EditText itemCategory = findViewById(R.id.itemCategoryEditText);
                EditText itemPriority = findViewById(R.id.itemPriorityEditText);
                EditText itemAmount = findViewById(R.id.itemAmountEditText);

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
                String itemCategoryString;
                if(itemCategory.getText().toString().equals("")){
                    itemCategoryString = getString(R.string.addItemAct_defaultCategoryName);
                } else {
                    itemCategoryString = itemCategory.getText().toString();
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

                boolean isInserted = myDb.addItem(shoppingList, itemNameString, itemCategoryString, itemPriorityInt, itemAmountString);
                if (isInserted) {
                    Toast.makeText(AddItemActivity.this, "Item added", Toast.LENGTH_LONG).show();
                    Intent itemsActivity = new Intent(AddItemActivity.this, ItemsActivity.class);
                    itemsActivity.putExtra("smg.SHOPPING_LIST", shoppingList);
                    startActivity(itemsActivity);
                } else {
                    Toast.makeText(AddItemActivity.this, "Adding failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // goes to parent activity on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
