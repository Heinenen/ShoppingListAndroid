package smg.shoppinglistapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import smg.logic.Item;

public class EditItemActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private String itemID;
    private String slID;
    private String shoppingList;
    private CheckBox itemPriority;
    private Item item;

    private EditText itemName;
    private EditText itemCategory;
    private EditText itemAmount;
    private EditText itemPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        setTitle(R.string.editItemAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.edit_item_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        this.myDb = new DatabaseHelper(this);
        this.itemID = getIntent().getStringExtra("smg.ITEM_ID");
        this.slID = getIntent().getStringExtra("smg.SL_ID");
        this.shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");
        this.item = getItemFromSQL(itemID);


        // set default text and set cursor to last position
        itemName = findViewById(R.id.editItemNameEditText);
        itemName.setText(item.getName());
        itemName.setSelection(item.getName().length());
        itemCategory = findViewById(R.id.editItemCategoryEditText);
        itemCategory.setText(item.getCategory());
        itemCategory.setSelection(item.getCategory().length());
        itemAmount = findViewById(R.id.editItemAmountEditText);
        itemAmount.setText(item.getAmount());
        itemAmount.setSelection(item.getAmount().length());
        itemPrice = findViewById(R.id.editItemPriceEditText);
        itemPrice.setText(item.getPrice().replace("€", ""));
        itemPrice.setSelection(item.getPrice().length() - 1);


        itemPriority = findViewById(R.id.editItemPriorityCheckBox);
        if(item.getPriority().equals("1")) itemPriority.setChecked(true);

        editItem();
//        deleteItem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    public void editItem(){
//        Button editItem = findViewById(R.id.editItemBtn);
        FloatingActionButton editItem = findViewById(R.id.editItemFAB);
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // takes previous values of the item as default values if nothing is typed into EditText
                String[] itemAttributes = new String[5];

                // default value for itemName
                if(itemName.getText().toString().equals("")){
                    itemAttributes[0] = item.getName();
                } else {
                    itemAttributes[0] = itemName.getText().toString();
                }

                // default value for itemCategory
                if(itemCategory.getText().toString().equals("")){
                    itemAttributes[1] = item.getCategory();
                } else {
                    itemAttributes[1] = itemCategory.getText().toString();
                }

                // default value for itemAmount
                if(itemAmount.getText().toString().equals("")){
                    itemAttributes[3] = item.getAmount();
                } else {
                    itemAttributes[3] = itemAmount.getText().toString();
                }

                if(itemPrice.getText().toString().equals("") || itemPrice.getText().toString().equals(" ")){
                    itemAttributes[4] = item.getPrice();
                } else {
                    itemAttributes[4] = itemPrice.getText().toString() + "€";
                }

                // default value for itemPriority
                int itemPriorityInt;
                if(itemPriority.isChecked()){
                    itemPriorityInt = 1;
                } else {
                    itemPriorityInt = 0;
                }




                boolean isInserted = myDb.updateItem(itemID, slID, itemAttributes[0], itemAttributes[1], itemAttributes[3], itemPriorityInt, itemAttributes[4]);

                if (isInserted) {
                    Toast.makeText(EditItemActivity.this, "Item edited", Toast.LENGTH_LONG).show();
                    Intent itemsActivity = new Intent(EditItemActivity.this, ItemsActivity.class);
                    itemsActivity.putExtra("smg.SL_ID", slID);
                    itemsActivity.putExtra("smg.SHOPPING_LIST", shoppingList);
                    startActivity(itemsActivity);
                } else {
                    Toast.makeText(EditItemActivity.this, "Editing failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    public void deleteItem(){
//        Button deleteItem = findViewById(R.id.deleteItemBtn);
//        deleteItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int deletedRows = myDb.deleteItem(itemID);
//                if (deletedRows > 0) {
//                    Toast.makeText(EditItemActivity.this, "Item deleted", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(EditItemActivity.this, "Deleting failed", Toast.LENGTH_LONG).show();
//                }
//                Intent itemsActivity = new Intent(EditItemActivity.this, ItemsActivity.class);
//                itemsActivity.putExtra("smg.SL_ID", slID);
//                startActivity(itemsActivity);
//            }
//        });
//    }

    public void deleteItemFromSQL(){
        int deletedRows = myDb.deleteItem(itemID);
        if (deletedRows > 0) {
            Toast.makeText(EditItemActivity.this, "Item deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditItemActivity.this, "Deleting failed", Toast.LENGTH_LONG).show();
        }
        Intent itemsActivity = new Intent(EditItemActivity.this, ItemsActivity.class);
        itemsActivity.putExtra("smg.SL_ID", slID);
        startActivity(itemsActivity);
    }


    public Item getItemFromSQL(String itemID){
        Cursor res = myDb.getItem(itemID);

        res.moveToNext();
        return new Item(
                res.getString(0),
                res.getString(2),
                res.getString(3),
                res.getString(4),
                res.getString(5),
                res.getString(6),
                res.getInt(7));
    }


    // goes to parent activity on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(EditItemActivity.this, ItemsActivity.class);
            intent.putExtra("smg.SL_ID", slID);
            intent.putExtra("smg.SHOPPING_LIST", shoppingList);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(EditItemActivity.this, ItemsActivity.class);
                intent.putExtra("smg.SL_ID", slID);
                intent.putExtra("smg.SHOPPING_LIST", shoppingList);
                startActivity(intent);
                return true;

            case R.id.action_delete_item:
                deleteItemFromSQL();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
