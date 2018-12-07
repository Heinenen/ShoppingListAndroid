package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import smg.databasehelpers.DatabaseHelper;

public class AddItemActivity extends AppCompatActivity {


    private String slID;
    private String shoppingList;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        setTitle(R.string.addItemAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.add_item_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        this.slID = getIntent().getStringExtra("smg.SL_ID");
        this.shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");
        this.myDb = new DatabaseHelper(this);

        fab();
    }


    // adds item
    public void fab(){
        FloatingActionButton addItem = findViewById(R.id.addItemFAB);
        // receives text given into EditTexts
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText itemName = findViewById(R.id.addItemNameEditText);
                EditText itemCategory = findViewById(R.id.addItemCategoryEditText);
                EditText itemAmount = findViewById(R.id.addItemAmountEditText);
                EditText itemPrice = findViewById(R.id.addItemPriceEditText);
                CheckBox itemPriority = findViewById(R.id.addItemPriorityCheckBox);


                // show alert dialog if no name is given, proceed if name is given
                String itemNameString;
                if(itemName.getText().toString().equals("")) {
                    showAlertDialog();
                } else {
                    // Item parameters
                    itemNameString = itemName.getText().toString();

                    // default value for category (-> "")
                    String itemCategoryString;
                    if (itemCategory.getText().toString().equals("")) {
                        itemCategoryString = getString(R.string.addItemAct_defaultCategoryName);
                    } else {
                        itemCategoryString = itemCategory.getText().toString();
                    }

                    // default value for amount (-> 1)
                    String itemAmountString;
                    if (itemAmount.getText().toString().equals("")) {
                        itemAmountString = getString(R.string.addItemAct_defaultAmount);
                    } else {
                        itemAmountString = itemAmount.getText().toString();
                    }

                    // default value for priority (-> 0)
                    int itemPriorityInt;
                    if (itemPriority.isChecked()) {
                        itemPriorityInt = 1;
                    } else {
                        itemPriorityInt = 0;
                    }

                    // default value for amount (-> "")
                    String itemPriceString = itemPrice.getText().toString();
                    if (itemPrice.getText().toString().equals("")) {
                        itemPriceString = " ";
                    } else {
                        itemPriceString = itemPriceString + "€";
                    }

                    // adds Item to SQL
                    boolean isInserted = myDb.addItem(slID, itemNameString, itemCategoryString, itemAmountString, itemPriorityInt, itemPriceString);
                    if (isInserted) {
                        Toast.makeText(AddItemActivity.this, R.string.toast_itemAdded, Toast.LENGTH_SHORT).show();
                        Intent itemsActivity = new Intent(AddItemActivity.this, ItemsActivity.class);
                        itemsActivity.putExtra("smg.SL_ID", slID);
                        itemsActivity.putExtra("smg.SHOPPING_LIST", shoppingList);
                        startActivity(itemsActivity);

                    } else {
                        Toast.makeText(AddItemActivity.this, R.string.toast_addingFailed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    // method for showing an AlertDialog when no name is given
    public void showAlertDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.actAddItem_alertTitle);
        alert.create().show();
    }


    // goes to ItemActivity on backKey
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(AddItemActivity.this, ItemsActivity.class);
            intent.putExtra("smg.SL_ID", slID);
            intent.putExtra("smg.SHOPPING_LIST", shoppingList);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    // goes to ItemActivity on .home
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home){
            Intent intent = new Intent(AddItemActivity.this, ItemsActivity.class);
            intent.putExtra("smg.SL_ID", slID);
            intent.putExtra("smg.SHOPPING_LIST", shoppingList);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
